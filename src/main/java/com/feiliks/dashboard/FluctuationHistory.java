package com.feiliks.dashboard;

public class FluctuationHistory<T, V extends Number> extends History<T, V> {

    private final AbstractMonitor.Task task;

    public FluctuationHistory(AbstractMonitor.Task task) {
        super(new IRealtimeEventHandler<T>() {
            @Override
            public void onExpired(Item<T> item) {
                task.sendMessage("Fluct_Realtime", new NotifierMessage<>(
                        "remove", String.valueOf(item.getKey()), null));
            }

            @Override
            public void onNew(Item<T> item) {
                task.sendMessage("Fluct_Realtime", new NotifierMessage<>(
                        "update", String.valueOf(item.getKey()), item.getData()));
            }
        }, new IAggHistoryEventHandler<V>() {
            @Override
            public void onPeriodExpired(String attr, Item<AggValues<V>> item) {
                task.sendMessage("Fluct_" + attr + "_Minutely", new NotifierMessage<>(
                        "remove", String.valueOf(item.getKey()), null));
            }

            @Override
            public void onNewPeriod(String attr, Item<AggValues<V>> item) {
                task.sendMessage("Fluct_" + attr + "_Minutely", new NotifierMessage<>(
                        "update", String.valueOf(item.getKey()), item.getData()));
            }
        }, new IAggHistoryEventHandler<V>() {
            @Override
            public void onPeriodExpired(String attr, Item<AggValues<V>> item) {
                task.sendMessage("Fluct_" + attr + "_Hourly", new NotifierMessage<>(
                        "remove", String.valueOf(item.getKey()), null));
            }

            @Override
            public void onNewPeriod(String attr, Item<AggValues<V>> item) {
                task.sendMessage("Fluct_" + attr + "_Hourly", new NotifierMessage<>(
                        "update", String.valueOf(item.getKey()), item.getData()));
            }
        });
        this.task = task;
    }

    public void exportResults() {
        task.exportResult("Fluct_Realtime", getRealtimeData());
        for (String attr : getAttributes()) {
            task.exportResult("Fluct_" + attr + "_Minutely",
                    getMinutelyData(attr));
            task.exportResult("Fluct_" + attr + "_Hourly",
                    getHourlyData(attr));
        }
    }

}

