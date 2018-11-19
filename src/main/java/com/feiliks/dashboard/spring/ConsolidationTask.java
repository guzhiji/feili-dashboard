package com.feiliks.dashboard.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ConsolidationTask {

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Autowired
    private ConsolidationDao dao;

    public static class HourlyStats {
        private long hour;
        private Map<String, Integer> data;

        public HourlyStats() {
        }

        public HourlyStats(Date hour) {
            this(hour, new HashMap<String, Integer>());
        }

        public HourlyStats(Date hour, Map<String, Integer> data) {
            this.hour = hour.getTime();
            this.data = data;
        }

        public long getHour() {
            return hour;
        }

        public void setHour(long hour) {
            this.hour = hour;
        }

        public Map<String, Integer> getData() {
            return data;
        }

        public void setData(Map<String, Integer> data) {
            this.data = data;
        }
    }

    private final LinkedList<HourlyStats> historicalStats = new LinkedList<>();
    private final Map<String, Integer> currentStats = new HashMap<>();
    private List<ConsolidationDao.OrderTrolley> table = null;
    private Map<String, Integer> currentHourStats = null;
    private Map<String, Set<String>> previousHourData = null, currentHourData = null;
    private Date currentHour = null;
    private boolean dataSent;

    private static Date getHour(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void nextHour(Date h) {
        currentHour = h;
        previousHourData = currentHourData;
        currentHourData = new HashMap<>();
        currentHourStats = new HashMap<>();
        for (ConsolidationDao.Status s : ConsolidationDao.Status.valuesExceptOther()) {
            currentHourData.put(s.name(), new HashSet<String>());
            currentHourStats.put(s.name(), 0);
        }
        if (historicalStats.size() > 24)
            historicalStats.poll();
        historicalStats.offer(new HourlyStats(currentHour, currentHourStats));
    }

    private boolean _computeHourlyStats() {
        boolean updated = false;
        for (String status : currentHourData.keySet()) {
            Set<String> prev = previousHourData == null ? null : previousHourData.get(status);
            if (prev == null) prev = Collections.emptySet();
            Set<String> newKeys = new HashSet<>(currentHourData.get(status));
            newKeys.removeAll(prev);
            int count = newKeys.size();
            if (currentHourStats.get(status) != count) {
                currentHourStats.put(status, count);
                updated = true;
            }
        }
        return updated;
    }

    private void computeHourlyStats(Iterable<ConsolidationDao.OrderTrolley> rows, boolean isNewHour) {
        for (ConsolidationDao.OrderTrolley row : rows) {
            String status = row.getStatus();
            String orderKey = row.getOrderKey();
            for (Map.Entry<String, Set<String>> entry : currentHourData.entrySet()) {
                if (status.equals(entry.getKey()))
                    entry.getValue().add(orderKey);
                else
                    entry.getValue().remove(orderKey);
            }
        }
        if (_computeHourlyStats() || isNewHour) {
            webSocketHandler.broadcast("line:" + currentHour.getTime() + ":" + stringifyStats(currentHourStats));
            dataSent = true;
        }
    }

    private void computeCurrentStats(Iterable<ConsolidationDao.OrderTrolley> rows) {
        Map<String, Set<String>> orderKeysByStatus = new HashMap<>();
        for (ConsolidationDao.Status s : ConsolidationDao.Status.values())
            orderKeysByStatus.put(s.name(), new HashSet<String>());
        for (ConsolidationDao.OrderTrolley row : rows) {
            Set<String> keys = orderKeysByStatus.get(row.getStatus());
            if (keys != null) keys.add(row.getOrderKey());
        }
        boolean updated = false;
        for (Map.Entry<String, Set<String>> e : orderKeysByStatus.entrySet()) {
            Integer count = e.getValue().size();
            if (!count.equals(currentStats.get(e.getKey()))) {
                currentStats.put(e.getKey(), count);
                updated = true;
            }
        }
        if (updated) {
            webSocketHandler.broadcast("pie:" + stringifyStats(currentStats));
            dataSent = true;
        }
    }

    private void partiallyRecoverData(Date curHour, Iterable<ConsolidationDao.OrderTrolley> rows) {
        Map<Date, Map<String, Set<String>>> groupByHour = new HashMap<>();
        for (ConsolidationDao.OrderTrolley row : rows) {
            Date h = new Date(row.getOpTime().getTime());
            h = getHour(h);
            boolean isNewHour = false;
            Map<String, Set<String>> statusOrders = groupByHour.get(h);
            if (statusOrders == null) {
                statusOrders = new HashMap<>();
                isNewHour = true;
            }
            Set<String> orders = statusOrders.get(row.getStatus());
            if (orders == null) {
                orders = new HashSet<>();
                orders.add(row.getOrderKey());
                statusOrders.put(row.getStatus(), orders);
            } else {
                orders.add(row.getOrderKey());
            }
            if (isNewHour) groupByHour.put(h, statusOrders);
        }
        List<HourlyStats> result = new ArrayList<>(24);
        previousHourData = new HashMap<>();
        currentHourData = new HashMap<>();
        currentHour = curHour;
        Calendar cal = Calendar.getInstance();
        cal.setTime(curHour);
        for (int h = cal.get(Calendar.HOUR_OF_DAY), i = 0; h >= 0; h--, i++) {
            cal.set(Calendar.HOUR_OF_DAY, h);
            Map<String, Set<String>> statusOrders = groupByHour.get(cal.getTime());
            HourlyStats stats = new HourlyStats(cal.getTime());
            if (i == 0) currentHourStats = stats.getData();
            for (ConsolidationDao.Status status : ConsolidationDao.Status.valuesExceptOther()) {
                String statusName = status.name();
                if (statusOrders == null || !statusOrders.containsKey(statusName)) {
                    stats.getData().put(statusName, 0);
                    switch (i) {
                        case 0: currentHourData.put(statusName, new HashSet<String>()); break;
                        case 1: previousHourData.put(statusName, new HashSet<String>()); break;
                    }
                } else {
                    Set<String> orders = statusOrders.get(statusName);
                    stats.getData().put(statusName, orders.size());
                    switch (i) {
                        case 0: currentHourData.put(statusName, orders); break;
                        case 1: previousHourData.put(statusName, orders); break;
                    }
                }
            }
            result.add(stats);
        }
        for (HourlyStats stats : result)
            historicalStats.addFirst(stats);
        webSocketHandler.broadcast("init:" + System.currentTimeMillis());
        dataSent = true;
    }

    private static String stringifyStats(Map<String, Integer> stats) {
        StringBuilder out = new StringBuilder();
        for (Map.Entry<String, Integer> item : stats.entrySet())
            out.append(item.getKey())
                .append('=')
                .append(item.getValue())
                .append(';');
        return out.toString();
    }

    public List<ConsolidationDao.OrderTrolley> getTable() {
        return table;
    }

    public Map<String, Integer> getCurrentStats() {
        return currentStats;
    }

    public List<HourlyStats> getHistoricalStats() {
        return historicalStats;
    }

    @Scheduled(fixedDelay = 5000)
    public void run() {

        Date hour = getHour(new Date());
        table = dao.getOrderTrolley();
        dataSent = false;
        if (currentHour == null) {
            historicalStats.clear();
            partiallyRecoverData(hour, table);
        } else {
            boolean isNewHour = false;
            if (hour.after(currentHour)) {
                nextHour(hour);
                isNewHour = true;
            }
            computeHourlyStats(table, isNewHour);
        }
        computeCurrentStats(table);
        if (!dataSent) webSocketHandler.broadcast("heartbeat:" + System.currentTimeMillis());

    }

}

