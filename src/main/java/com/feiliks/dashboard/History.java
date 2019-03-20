package com.feiliks.dashboard;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


public class History<T, V extends Number> {

    public static class Item<T> {
        private long key;
        private T data;

        public Item() {}

        public Item(long key, T data) {
            this.key = key;
            this.data = data;
        }

        public long getKey() {
            return key;
        }

        public void setKey(long key) {
            this.key = key;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

    public static class AggValues<V> {
        private double avg;
        private V max;
        private V min;

        public AggValues() {}

        public AggValues(double avg, V max, V min) {
            this.setAvg(avg);
            this.setMax(max);
            this.setMin(min);
        }

        public double getAvg() {
            return avg;
        }

        public void setAvg(double avg) {
            this.avg = avg;
        }

        public V getMax() {
            return max;
        }

        public void setMax(V max) {
            this.max = max;
        }

        public V getMin() {
            return min;
        }

        public void setMin(V min) {
            this.min = min;
        }
    }

    private interface IPeriod {
        long trunc(long ts);
        int length();
    }

    public interface IAggHistoryEventHandler<V> {
        void onPeriodExpired(String attr, Item<AggValues<V>> item);
        void onNewPeriod(String attr, Item<AggValues<V>> item);
    }

    public interface IRealtimeEventHandler<T> {
        void onExpired(Item<T> item);
        void onNew(Item<T> item);
    }

    private static class AggHistoryInternal<V extends Number> {
        private final String attribute;
        private final IPeriod period;
        private final Queue<Item<AggValues<V>>> queue = new ConcurrentLinkedQueue<>();
        private final IAggHistoryEventHandler<V> eventHandler;
        private Long time = null;
        private BigDecimal sum = null;
        private int count = 0;
        private V max = null;
        private V min = null;

        public AggHistoryInternal(
                String attr, IPeriod period, IAggHistoryEventHandler<V> handler) {
            this.attribute = attr;
            this.period = period;
            this.eventHandler = handler;
        }

        public String getAttribute() {
            return attribute;
        }

        public void clear() {
            queue.clear();
            time = null;
            sum = null;
            count = 0;
            max = null;
            min = null;
        }

        public int getSize() {
            return queue.size();
        }

        public void add(long ts, V value) {
            long truncTs = period.trunc(ts);
            if (time == null || truncTs > time) {
                if (time != null) {
                    while (queue.size() >= period.length())
                        eventHandler.onPeriodExpired(attribute, queue.poll());
                    // save agg for the last period of time
                    double avg = sum.divide(BigDecimal.valueOf(count),
                            3, RoundingMode.HALF_UP).doubleValue();
                    Item<AggValues<V>> newItem = new Item<>(
                            time, new AggValues<>(avg, max, min));
                    queue.offer(newItem);
                    eventHandler.onNewPeriod(attribute, newItem);
                }
                // init for the new period
                time = truncTs;
                sum = BigDecimal.ZERO;
                count = 0;
                max = null;
                min = null;
            }
            // compute sum, count, max, and min for value
            double d = value.doubleValue();
            sum = sum.add(BigDecimal.valueOf(d));
            count++;
            if (max == null || max.doubleValue() < d)
                max = value;
            if (min == null || min.doubleValue() > d)
                min = value;
        }

        public Iterable<Item<AggValues<V>>> getData() {
            return queue;
        }

    }

    public static class AggHistory<V extends Number> {
        private final String attribute;
        private final AggHistoryInternal<V> minutelyData;
        private final AggHistoryInternal<V> hourlyData;

        public AggHistory(
                String attr,
                IAggHistoryEventHandler<V> minutelyHandler,
                IAggHistoryEventHandler<V> hourlyHandler) {

            attribute = attr;

            minutelyData = new AggHistoryInternal<>(attr, new IPeriod() {
                @Override
                public long trunc(long ts) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(ts);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    return cal.getTimeInMillis();
                }

                @Override
                public int length() {
                    return 120;
                }
            }, minutelyHandler);

            hourlyData = new AggHistoryInternal<>(attr, new IPeriod() {
                @Override
                public long trunc(long ts) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(ts);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    return cal.getTimeInMillis();
                }

                @Override
                public int length() {
                    return 120;
                }
            }, hourlyHandler);

        }

        public String getAttribute() {
            return attribute;
        }

        public void add(long ts, V value) {
            minutelyData.add(ts, value);
            hourlyData.add(ts, value);
        }

        public Iterable<Item<AggValues<V>>> getMinutelyData() {
            return minutelyData.getData();
        }

        public Iterable<Item<AggValues<V>>> getHourlyData() {
            return hourlyData.getData();
        }

        public void clear() {
            minutelyData.clear();
            hourlyData.clear();
        }

    }

    private final Queue<Item<T>> realtimeData = new ConcurrentLinkedQueue<>();
    private final Map<String, AggHistory<V>> aggData = new HashMap<>();
    private final IRealtimeEventHandler<T> realtimeEventHandler;
    private final IAggHistoryEventHandler<V> minutelyEventHandler;
    private final IAggHistoryEventHandler<V> hourlyEventHandler;

    public History(
            IRealtimeEventHandler<T> rHandler,
            IAggHistoryEventHandler<V> mHandler,
            IAggHistoryEventHandler<V> hHandler) {
        realtimeEventHandler = rHandler;
        minutelyEventHandler = mHandler;
        hourlyEventHandler = hHandler;
    }

    public void add(long ts, T data) {

        while (realtimeData.size() >= 120)
            realtimeEventHandler.onExpired(
                realtimeData.poll());
        Item<T> newItem = new Item<>(ts, data);
        realtimeData.offer(newItem);

        for (Method method : data.getClass().getMethods()) {
            int modifiers = method.getModifiers();
            if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers))
                continue;
            if (!method.getName().startsWith("get"))
                continue;
            if (method.getParameterCount() != 0)
                continue;
            Class<?> rt = method.getReturnType();
            if (!rt.isPrimitive() &&
                    !Number.class.isAssignableFrom(rt))
                continue;

            String attr = method.getName().substring(3);
            AggHistory<V> agg = aggData.get(attr);
            if (agg == null) { // encounters new attribute
                agg = new AggHistory<>(attr,
                        minutelyEventHandler,
                        hourlyEventHandler);
                aggData.put(attr, agg);
            }
            try {
                Object n = method.invoke(data);
                if (n != null) agg.add(ts, (V) n);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }

        realtimeEventHandler.onNew(newItem);
    }

    public Iterable<Item<T>> getRealtimeData() {
        return realtimeData;
    }

    public Set<String> getAttributes() {
        return aggData.keySet();
    }

    public Iterable<Item<AggValues<V>>> getMinutelyData(String attr) {
        AggHistory<V> history = aggData.get(attr);
        if (history == null) return null;
        return history.getMinutelyData();
    }

    public Iterable<Item<AggValues<V>>> getHourlyData(String attr) {
        AggHistory<V> history = aggData.get(attr);
        if (history == null) return null;
        return history.getHourlyData();
    }

    public void clear() {
        realtimeData.clear();
        aggData.clear();
    }

}
