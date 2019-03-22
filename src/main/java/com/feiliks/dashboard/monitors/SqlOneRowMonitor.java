package com.feiliks.dashboard.monitors;

import com.feiliks.dashboard.AbstractMonitor;
import com.feiliks.dashboard.History;
import com.feiliks.dashboard.NotifierMessage;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;


public class SqlOneRowMonitor extends AbstractMonitor {

    public final class Task extends AbstractMonitor.Task {

        private final History<Map<String, Object>, Double> history = new History<>(
                new History.IRealtimeEventHandler<Map<String, Object>>() {
                    @Override
                    public void onExpired(History.Item<Map<String, Object>> item) {
                        sendMessage("History_Realtime", new NotifierMessage<>(
                                "remove", String.valueOf(item.getKey()), null));
                    }

                    @Override
                    public void onNew(History.Item<Map<String, Object>> item) {
                        sendMessage("History_Realtime", new NotifierMessage<>(
                                "update", String.valueOf(item.getKey()), item.getData()));
                    }
                },
                new History.IAggHistoryEventHandler<Double>() {
                    @Override
                    public void onPeriodExpired(String attr, History.Item<History.AggValues<Double>> item) {
                        sendMessage("History_" + attr + "_Minutely", new NotifierMessage<>(
                                "remove", String.valueOf(item.getKey()), null));
                    }

                    @Override
                    public void onNewPeriod(String attr, History.Item<History.AggValues<Double>> item) {
                        sendMessage("History_" + attr + "_Minutely", new NotifierMessage<>(
                                "update", String.valueOf(item.getKey()), item.getData()));
                    }
                },
                new History.IAggHistoryEventHandler<Double>() {
                    @Override
                    public void onPeriodExpired(String attr, History.Item<History.AggValues<Double>> item) {
                        sendMessage("History_" + attr + "_Hourly", new NotifierMessage<>(
                                "remove", String.valueOf(item.getKey()), null));
                    }

                    @Override
                    public void onNewPeriod(String attr, History.Item<History.AggValues<Double>> item) {
                        sendMessage("History_" + attr + "_Hourly", new NotifierMessage<>(
                                "update", String.valueOf(item.getKey()), item.getData()));
                    }
                });

        @Override
        public void run() {

            String sql = (String) getMonitorInfo().readConfig("dbSql");
            DataSource ds = getDataSource();
            Map<String, Object> out = null;

            try (Connection conn = ds.getConnection()) {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    long ts = System.currentTimeMillis();
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            out = new HashMap<>();
                            ResultSetMetaData metaData = rs.getMetaData();
                            int c = metaData.getColumnCount();
                            for (int i = 1; i <= c; i++) {
                                out.put(metaData.getColumnLabel(i),
                                        rs.getObject(i));
                            }
                            history.add(ts, out);
                            exportResult("History_Realtime",
                                    history.getRealtimeData());
                        }
                    }
                    sendMessage("Result", new NotifierMessage<>(
                            "update", String.valueOf(ts), out));

                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                exportResult("Result", out);
            }

        }
    }

    public SqlOneRowMonitor() {
        super(SqlOneRowMonitor.class, Task.class, true);
        registerResultSource("Result", "obj");
        registerResultSource("History_Realtime", "obj-list");
        registerMessageSource("Result", "obj");
        registerMessageSource("History_Realtime", "obj-list");
    }

}

