package com.feiliks.dashboard.monitors;

import com.feiliks.dashboard.spring.impl.AbstractMonitorNotifier;

import javax.sql.DataSource;
import java.sql.*;


public class SqlOneRowMonitor extends AbstractMonitorNotifier {

    @Override
    public void run() {

        try {
            DataSource ds = getDatabase();
            Connection conn = ds.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("");
            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            // metaData.getColumnLabel();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}

