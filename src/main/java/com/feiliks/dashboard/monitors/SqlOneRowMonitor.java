package com.feiliks.dashboard.monitors;

import com.feiliks.dashboard.spring.impl.AbstractMonitorNotifier;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;


public class SqlOneRowMonitor extends AbstractMonitorNotifier {

    @Override
    public void run() {

        String sql = (String) getMonitor().readConfig("dbSql");
        DataSource ds = getDatabase();
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

                notifyClient("");

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            exportDataSource("result", out);
        }

    }

}

