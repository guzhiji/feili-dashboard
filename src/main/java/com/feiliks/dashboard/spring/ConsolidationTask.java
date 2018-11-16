package com.feiliks.dashboard.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ConsolidationTask {

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Autowired
    private ConsolidationDao dao;

    public static class HourlyStats {
        private long hour;
        private Map<String, Long> data;

        public HourlyStats(long hour, Map<String, Long> data) {
            this.hour = hour;
            this.data = data;
        }

        public long getHour() {
            return hour;
        }

        public void setHour(long hour) {
            this.hour = hour;
        }

        public Map<String, Long> getData() {
            return data;
        }

        public void setData(Map<String, Long> data) {
            this.data = data;
        }
    }

    private List<HourlyStats> historicalStats = null;
    private final Map<String, Long> currentStats = new HashMap<>();
    private List<ConsolidationDao.OrderTrolley> table = null;

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

    public Map<String, Long> getCurrentStats() {
        return currentStats;
    }

    public List<HourlyStats> getHistoricalStats() {
        return historicalStats;
    }

    @Scheduled(fixedDelay = 5000)
    public void run() {

        table = dao.getTable();

        Set<String> statusList = new HashSet<>(currentStats.keySet());
        for (ConsolidationDao.Status status : ConsolidationDao.Status.values())
            statusList.add(status.name());
        for (ConsolidationDao.StatusCount count : dao.getPie()) {
            statusList.remove(count.getStatus());
            currentStats.put(count.getStatus(), count.getCount());
        }
        for (String status : statusList)
            currentStats.put(status, 0L);

        Map<Long, HourlyStats> groupByHour = new HashMap<>();
        for (ConsolidationDao.TimelyStatusCount count : dao.getLine()) {
            HourlyStats stats = groupByHour.get(count.getTime());
            if (stats == null) {
                Map<String, Long> counts = new HashMap<>();
                counts.put(count.getStatus(), count.getCount());
                stats = new HourlyStats(count.getTime(), counts);
                groupByHour.put(count.getTime(), stats);
            } else {
                stats.getData().put(count.getStatus(), count.getCount());
            }
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        for (int h = cal.get(Calendar.HOUR_OF_DAY); h >= 0; h--) {
            cal.set(Calendar.HOUR_OF_DAY, h);
            long t = cal.getTimeInMillis();
            HourlyStats stats = groupByHour.get(t);
            if (stats == null) {
                Map<String, Long> counts = new HashMap<>();
                for (ConsolidationDao.Status status : ConsolidationDao.Status.values()) {
                    if (status == ConsolidationDao.Status.OTHER) continue;
                    counts.put(status.name(), 0L);
                }
                stats = new HourlyStats(t, counts);
                groupByHour.put(t, stats);
            } else {
                for (ConsolidationDao.Status status : ConsolidationDao.Status.values()) {
                    if (status == ConsolidationDao.Status.OTHER) continue;
                    Map<String, Long> counts = stats.getData();
                    if (!counts.containsKey(status.name()))
                        counts.put(status.name(), 0L);
                }
            }
        }
        List<HourlyStats> sortedStats = new ArrayList<>(groupByHour.values());
        Collections.sort(sortedStats, new Comparator<HourlyStats>() {
            @Override
            public int compare(HourlyStats a, HourlyStats b) {
                return Long.compare(a.getHour(), b.getHour());
            }
        });
        historicalStats = sortedStats;

    }

}

