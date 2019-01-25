package com.feiliks.dashboard.monitors;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.feiliks.dashboard.spring.impl.AbstractMonitor;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.*;


public class SqlMultiRowsMonitor extends AbstractMonitor {

    @Override
    public void run() {

        String sql = (String) getMonitor().readConfig("dbSql");
        DataSource ds = getDatabase();

        try {

            try (Connection conn = ds.getConnection()) {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
                        exportDataSourcePreformatted(
                                "result", sw.toString());

                    }
                }
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            exportDataSource("result", null);
        }

    }

}
