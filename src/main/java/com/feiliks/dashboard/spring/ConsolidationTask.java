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
        private Date hour;
        private Map<String, Integer> data;

        public HourlyStats(Date hour, Map<String, Integer> data) {
            this.hour = hour;
            this.data = data;
        }

        public Date getHour() {
            return hour;
        }

        public void setHour(Date hour) {
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
    private boolean hasUpdate = false;

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
        for (ConsolidationDao.Status s : ConsolidationDao.Status.values()) {
            currentHourData.put(s.name(), new HashSet<String>());
            currentHourStats.put(s.name(), 0);
        }
        if (historicalStats.size() > 24)
            historicalStats.poll();
        historicalStats.offer(new HourlyStats(currentHour, currentHourStats));
    }

    private void process(ConsolidationDao.OrderTrolley row) {
        String key = row.getOrderKey();
        String status = row.getStatus();
        Set<String> cur = currentHourData.get(status);
        if (cur == null) return;
        Set<String> prev = previousHourData == null ? null : previousHourData.get(status);
        if ((prev == null || !prev.contains(key)) && !cur.contains(key)) { // new order key
            Integer count = currentHourStats.get(status);
            if (count == null) count = 0;
            currentHourStats.put(status, count + 1);
            hasUpdate = true;
        }
        cur.add(key);
    }

    private void processAll(Iterable<ConsolidationDao.OrderTrolley> rows) {
        hasUpdate = false;
        for (ConsolidationDao.OrderTrolley row : rows)
            process(row);
        if (hasUpdate)
            webSocketHandler.broadcast("line:" + currentHour.getTime() + ":" + stringifyStats(currentHourStats));
    }

    private void computeCurrentStats(Iterable<ConsolidationDao.OrderTrolley> rows) {
        Map<String, Set<String>> data = new HashMap<>();
        for (ConsolidationDao.Status s : ConsolidationDao.Status.values()) {
            currentStats.put(s.name(), 0);
            data.put(s.name(), new HashSet<String>());
        }
        for (ConsolidationDao.OrderTrolley row : rows) {
            Set<String> keys = data.get(row.getStatus());
            if (keys != null) keys.add(row.getOrderKey());
        }
        for (Map.Entry<String, Set<String>> e : data.entrySet())
            currentStats.put(e.getKey(), e.getValue().size());
        webSocketHandler.broadcast("pie:" + stringifyStats(currentStats));
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
        if (currentHour == null || hour.after(currentHour)) {
            nextHour(hour);
        }
        processAll(table);
        computeCurrentStats(table);

    }

}

