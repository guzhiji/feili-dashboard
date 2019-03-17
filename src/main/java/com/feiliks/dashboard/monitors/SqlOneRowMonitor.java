package com.feiliks.dashboard.monitors;

import com.feiliks.dashboard.AbstractMonitor;
import com.feiliks.dashboard.NotifierMessage;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;


public class SqlOneRowMonitor extends AbstractMonitor {

    public final class Task extends AbstractMonitor.Task {

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
        registerMessageSource("Result", "obj");
    }

}

