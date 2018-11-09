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

    private List<ConsolidationDao.OrderTrolley> table = null;
    private final Map<String, Integer> currentStats = new HashMap<>();
    private Date currentHour = null;
    private Map<String, Set<String>> currentHourStats = null;
    private final LinkedList<HourlyStats> historicalStats = new LinkedList<>();

    private static Date getHour(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private static void mapOrdersByStatus(
            List<ConsolidationDao.OrderTrolley> data,
            Map<String, Set<String>> out) {
        for (ConsolidationDao.OrderTrolley ot : data) {
            String status = ot.getStatus();
            Set<String> orderKeys = out.get(status);
            if (orderKeys == null) {
                orderKeys = new HashSet<>();
                out.put(status, orderKeys);
            }
            orderKeys.add(ot.getOrderKey());
        }
    }

    private static void computeCounts(Map<String, Set<String>> data, Map<String, Integer> counts) {
        for (Map.Entry<String, Set<String>> entry : data.entrySet())
            counts.put(entry.getKey(), entry.getValue().size());
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

        table = dao.getOrderTrolley();
        Date hour = getHour(new Date());
        if (currentHour == null || currentHourStats == null) {
            // the first run
            currentHour = hour;
            currentHourStats = new HashMap<>();
        } else if (hour.after(currentHour)) {
            // a newer hour
            if (historicalStats.size() > 24)
                historicalStats.poll();
            Map<String, Integer> data = new HashMap<>();
            computeCounts(currentHourStats, data);
            historicalStats.offer(new HourlyStats(currentHour, data));
            currentHour = hour;
            currentHourStats = new HashMap<>();
        }
        // accumulate
        mapOrdersByStatus(table, currentHourStats);

        // current
        Map<String, Set<String>> stats = new HashMap<>();
        mapOrdersByStatus(table, stats);
        computeCounts(stats, currentStats);

    }

}

