package com.feiliks.dashboard.spring;

import java.util.*;

public class PerfMonitor {

    private final static int MAX_SIZE = 1000;
    private final static Map<String, PerfMonitor> MONITORS = new HashMap<>();

    public static Set<String> getMonitors() {
        return MONITORS.keySet();
    }

    public static boolean exists(String target) {
        return MONITORS.containsKey(target);
    }

    public static void remove(String target) {
        MONITORS.remove(target);
    }

    public static PerfMonitor getInstance(String target) {
        if (MONITORS.containsKey(target)) {
            return MONITORS.get(target);
        } else {
            PerfMonitor mon = new PerfMonitor(target);
            MONITORS.put(target, mon);
            return mon;
        }
    }

    public static class Agg {
        private final long time;
        private final SortedSet<Long> data;

        public Agg(long t) {
            time = t;
            data = new TreeSet<>();
        }

        public void add(long value) {
            data.add(value);
        }

        public long getTime() {
            return time;
        }

        public int count() {
            return data.size();
        }

        public Float mean() {
            if (data.isEmpty())
                return null;
            long total = 0L;
            for (Long value : data)
                total += value;
            return 1.0F * total / data.size();
        }

        public Long min() {
            try {
                return data.first();
            } catch (NoSuchElementException ex) {
                return null;
            }
        }

        public Long max() {
            try {
                return data.last();
            } catch (NoSuchElementException ex) {
                return null;
            }
        }

        public Float percentile(int percentage) {
            int total = data.size();
            if (total == 0) return null;
            double target = percentage * total / 100.0;
            int f = (int) Math.floor(target);
            int c = (int) Math.ceil(target);
            int i = 0;
            if (f == c) {
                long sum = 0L;
                c++;
                for (Long value : data) {
                    i++;
                    if (i == f) {
                        sum += value;
                    } else if (i == c) {
                        sum += value;
                        return sum / 2.0F;
                    }
                }
            } else {
                for (Long value : data) {
                    if (++i == c)
                        return value.floatValue();
                }
            }
            return null;
        }

        public AggInfo getInfo() {
            if (data.isEmpty())
                return null;
            AggInfo out = new AggInfo();
            out.time = time;
            out.min = min();
            out.max = max();
            out.mean = mean();
            out.median = percentile(50);
            out.ninetiethPercentile = percentile(90);
            out.tenthPercentile = percentile(10);
            return out;
        }

    }

    public static class AggInfo {
        private long time;
        private long min;
        private long max;
        private float mean;
        private float median;
        private float ninetiethPercentile;
        private float tenthPercentile;

        public long getTime() {
            return time;
        }

        public long getMin() {
            return min;
        }

        public long getMax() {
            return max;
        }

        public float getMean() {
            return mean;
        }

        public float getMedian() {
            return median;
        }

        public float getNinetiethPercentile() {
            return ninetiethPercentile;
        }

        public float getTenthPercentile() {
            return tenthPercentile;
        }
    }

    private static long toHour(long t) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(t);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        return cal.getTimeInMillis();
    }

    private static long toMinute(long t) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(t);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTimeInMillis();
    }

    private final String target;
    private final Deque<Map.Entry<Long, Long>> measureData = new LinkedList<>();
    private final Deque<AggInfo> hourlyData = new LinkedList<>();
    private final Deque<AggInfo> minutelyData = new LinkedList<>();
    private Agg currentHour = null;
    private Agg currentMinute = null;

    private PerfMonitor(String t) {
        target = t;
    }

    public void clear() {
        measureData.clear();
        hourlyData.clear();
        minutelyData.clear();
        currentMinute = null;
        currentHour = null;
    }

    public String getTarget() {
        return target;
    }

    public Collection<Map.Entry<Long, Long>> getMeasureData() {
        return measureData;
    }

    public Collection<AggInfo> getHourlyData() {
        return hourlyData;
    }

    public Collection<AggInfo> getMinutelyData() {
        return minutelyData;
    }

    public long measure(long n) {
        long t = System.currentTimeMillis();

        long h = toHour(t);
        if (currentHour == null || currentHour.getTime() != h) {
            if (currentHour != null) {
                if (hourlyData.size() > MAX_SIZE)
                    hourlyData.poll();
                AggInfo aggInfo = currentHour.getInfo();
                if (aggInfo != null)
                    hourlyData.add(aggInfo);
            }
            currentHour = new Agg(h);
        }
        currentHour.add(n);

        long m = toMinute(t);
        if (currentMinute == null || currentMinute.getTime() != m) {
            if (currentMinute != null) {
                if (minutelyData.size() > MAX_SIZE)
                    minutelyData.poll();
                AggInfo aggInfo = currentMinute.getInfo();
                if (aggInfo != null)
                    minutelyData.add(aggInfo);
            }
            currentMinute = new Agg(m);
        }
        currentMinute.add(n);

        if (measureData.size() > MAX_SIZE)
            measureData.poll();
        measureData.offer(new AbstractMap.SimpleImmutableEntry<>(t, n));
        return t;
    }

}
