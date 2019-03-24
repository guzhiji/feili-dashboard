package com.feiliks.dashboard.monitors;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.feiliks.dashboard.AbstractMonitor;
import com.feiliks.dashboard.PerformanceHistory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.*;


public class SqlMultiRowsMonitor extends AbstractMonitor {

    public final class Task extends AbstractMonitor.Task {

        private final PerformanceHistory perf = new PerformanceHistory(this);

        @Override
        public void run() {

            String sql = (String) getMonitorInfo().readConfig("dbSql");
            DataSource ds = getDataSource();
            perf.reset();

            perf.start("Conn");
            try (Connection conn = ds.getConnection()) {
                perf.stop("Conn");
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    perf.start("Exec");
                    try (ResultSet rs = pstmt.executeQuery()) {

                        StringWriter sw = new StringWriter();
                        JsonFactory jf = new JsonFactory();
                        JsonGenerator jg = jf.createGenerator(sw);
                        jg.writeStartArray();

                        ResultSetMetaData metaData = rs.getMetaData();
                        int c = metaData.getColumnCount();
                        while (rs.next()) {
                            jg.writeStartObject();
                            for (int i = 1; i <= c; i++) {
                                jg.writeObjectField(
                                        metaData.getColumnLabel(i),
                                        rs.getObject(i));
                            }
                            jg.writeEndObject();
                        }

                        jg.writeEndArray();
                        exportPreformattedResult(
                                "Result", sw.toString());
                        perf.stop("Exec");
                    }
                } // end: PreparedStatement
            } catch (SQLTimeoutException e) {
                perf.stop("Conn");
            } catch (SQLException | IOException e) {
                e.printStackTrace();
                exportResult("Result", null);
            } finally {
                perf.finish();
                perf.exportResults();
            } // end: Connection

        }
    }

    public SqlMultiRowsMonitor() {
        super(SqlMultiRowsMonitor.class, Task.class, true);
        registerResultSource("Result", "obj-list");
        registerResultSource("Perf_Realtime", "obj-list");
        registerResultSource("Perf_Conn_Minutely", "obj-list");
        registerResultSource("Perf_Exec_Minutely", "obj-list");
        registerResultSource("Perf_Conn_Hourly", "obj-list");
        registerResultSource("Perf_Exec_Hourly", "obj-list");
        registerMessageSource("Perf_Realtime", "obj-list");
        registerMessageSource("Perf_Conn_Minutely", "obj-list");
        registerMessageSource("Perf_Exec_Minutely", "obj-list");
        registerMessageSource("Perf_Conn_Hourly", "obj-list");
        registerMessageSource("Perf_Exec_Hourly", "obj-list");
    }

}
