package com.feiliks.dashboard.monitors;

import com.feiliks.dashboard.AbstractMonitor;
import com.feiliks.dashboard.FluctuationHistory;
import com.feiliks.dashboard.History;
import com.feiliks.dashboard.PerformanceHistory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;


public class SqlOneRowMonitor extends AbstractMonitor {

    public final class Task extends AbstractMonitor.Task {

        private final PerformanceHistory perf = new PerformanceHistory(this);
        private final History<Map<String, Object>, Double> fluct = new FluctuationHistory<>(this);

        @Override
        public void run() {

            String sql = (String) getMonitorInfo().readConfig("dbSql");
            DataSource ds = getDataSource();
            Map<String, Object> out = null;
            perf.reset();

            perf.start("Conn");
            try (Connection conn = ds.getConnection()) {
                perf.stop("Conn");
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    perf.start("Exec");
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
                            fluct.add(ts, out);
                            exportResult("Fluct_Realtime",
                                    fluct.getRealtimeData());
                        }
                        perf.stop("Exec");
                    }

                } // end: PreparedStatement
            } catch (SQLTimeoutException e) {
                perf.stop("Conn");
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                exportResult("Result", out);
                perf.finish();
                perf.exportResults();
            } // end: Connection

        }
    }

    public SqlOneRowMonitor() {
        super(SqlOneRowMonitor.class, Task.class, true);
        registerResultSource("Result", "obj");
        registerResultSource("Fluct_Realtime", "obj-list");
        registerResultSource("Perf_Realtime", "obj-list");
        registerResultSource("Perf_Conn_Minutely", "obj-list");
        registerResultSource("Perf_Exec_Minutely", "obj-list");
        registerResultSource("Perf_Conn_Hourly", "obj-list");
        registerResultSource("Perf_Exec_Hourly", "obj-list");
        registerMessageSource("Fluct_Realtime", "obj-list");
        registerMessageSource("Perf_Realtime", "obj-list");
        registerMessageSource("Perf_Conn_Minutely", "obj-list");
        registerMessageSource("Perf_Exec_Minutely", "obj-list");
        registerMessageSource("Perf_Conn_Hourly", "obj-list");
        registerMessageSource("Perf_Exec_Hourly", "obj-list");
    }

}

