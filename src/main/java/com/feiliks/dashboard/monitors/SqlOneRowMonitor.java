package com.feiliks.dashboard.monitors;

import com.feiliks.dashboard.AbstractMonitor;

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

                    notifyClient("", "");

                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                exportResult("result", out);
            }

        }
    }

    public SqlOneRowMonitor() {
        super(Task.class, true);
    }

}

