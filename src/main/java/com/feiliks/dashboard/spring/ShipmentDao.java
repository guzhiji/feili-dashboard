package com.feiliks.dashboard.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public class ShipmentDao {

    public enum Status {
        UNFINISHED, WAITING, APPOINTMENT
    }

    public static class Trolley {
        private String trolleyId;
        private String factory;
        private String line;
        private int boxQty;
        private String appointmentKey;
        String status;

        public String getTrolleyId() {
            return trolleyId;
        }

        public String getFactory() {
            return factory;
        }

        public String getLine() {
            return line;
        }

        public int getBoxQty() {
            return boxQty;
        }

        public String getAppointmentKey() {
            return appointmentKey;
        }

        public String getStatus() {
            return status;
        }
    }

    public static class TrolleyOrder {
        private String trolleyId;
        private String orderKey;
        private String orderStatus;
        private String putawayZone;

        public TrolleyOrder() {}

        public TrolleyOrder(
                String trolleyId,
                String orderKey,
                String orderStatus,
                String putawayZone) {
            this.trolleyId = trolleyId;
            this.orderKey = orderKey;
            this.orderStatus = orderStatus;
            this.putawayZone = putawayZone;
        }

        public String getTrolleyId() {
            return trolleyId;
        }

        public String getOrderKey() {
            return orderKey;
        }

        public String getOrderStatus() {
            return orderStatus;
        }

        public String getPutawayZone() {
            return putawayZone;
        }
    }

    public static class Appointment {
        private String key;
        private String factory;
        private String line;
        private Long start;

        public String getKey() {
            return key;
        }

        public String getFactory() {
            return factory;
        }

        public String getLine() {
            return line;
        }

        public Long getStart() {
            return start;
        }

        @Override
        public String toString() {
            return key + ':' + start + ':' + factory + '-' + line;
        }

        @Override
        public int hashCode() {
            int code = 0;
            if (key != null)
                code += key.hashCode();
            return code;
        }

        @Override
        public boolean equals(Object other) {
            if (key == null)
                return super.equals(other);
            if (other instanceof Appointment)
                return key.equals(((Appointment) other).key);
            return false;
        }

    }

    @Autowired
    private JdbcTemplate jdbc;

    private final static String sqlTrolleys = "select distinct" +
            "    dd.DROPID trolley_id," +
            "    o.CONSIGNEEKEY factory," +
            "    o.TRADINGPARTNER line," +
            "    t.box_qty," +
            "    o.APPOINTMENTKEY appointment_key " +
            "from ORDERS o" +
            "    inner join PICKDETAIL p on p.ORDERKEY = o.ORDERKEY" +
            "    inner join LOC l on l.LOC = p.LOC and l.PUTAWAYZONE = 'LKSHIP'" +
            "    inner join DROPIDDETAIL dd on dd.CHILDID = p.DROPID" +
            "    inner join DROPID d on d.DROPID = dd.DROPID and d.DROPIDTYPE = '10'" +
            "    left join (" +
            "        select " +
            "            dd.DROPID trolley_id," +
            "            count(distinct p.DROPID) box_qty" +
            "        from PICKDETAIL p" +
            "            inner join LOC l on l.LOC = p.LOC and l.PUTAWAYZONE = 'LKSHIP'" +
            "            inner join DROPIDDETAIL dd on dd.CHILDID = p.DROPID" +
            "                inner join DROPID d on d.DROPID = dd.DROPID and d.DROPIDTYPE = '10'" +
            "        group by dd.DROPID" +
            "    ) t on t.trolley_id = dd.DROPID";

    private final static String sqlTrolleyOrders = "select " +
            "    dd.DROPID trolley_id," +
            "    o.ORDERKEY order_key," +
            "    o.STATUS order_status," +
            "    l.PUTAWAYZONE " +
            "from ORDERS o" +
            "    inner join PICKDETAIL p on p.ORDERKEY = o.ORDERKEY" +
            "    inner join LOC l on l.LOC = p.LOC" +
            "    inner join AREADETAIL ad on ad.PUTAWAYZONE = l.PUTAWAYZONE and ad.AREAKEY='CQ2'" +
            "    inner join DROPIDDETAIL dd on dd.CHILDID = p.DROPID" +
            "    inner join DROPID d on d.DROPID = dd.DROPID and d.DROPIDTYPE = '10'";

    private final static String sqlAppointments = "select distinct" +
            "    a.APPOINTMENTKEY appointment_key," +
            "    max(o.CONSIGNEEKEY) factory," +
            "    max(o.TRADINGPARTNER) line," +
            "    a.ADDDATE start_time " +
            "from APPOINTMENT a" +
            "    inner join ORDERS o on o.APPOINTMENTKEY = a.APPOINTMENTKEY" +
            "        inner join PICKDETAIL p on p.ORDERKEY = o.ORDERKEY" +
            "        inner join LOC l on l.LOC = p.LOC and l.PUTAWAYZONE = 'LKSHIP' " +
            "where a.STATUS <> '5COMP' " +
            "group by a.APPOINTMENTKEY, a.ADDDATE " +
            "order by start_time";

    public List<Trolley> getTrolleys() {
        return jdbc.query(
                sqlTrolleys,
                new RowMapper<Trolley>() {
                    @Override
                    public Trolley mapRow(ResultSet resultSet, int i) throws SQLException {
                        Trolley t = new Trolley();
                        t.trolleyId = resultSet.getString(1);
                        t.factory = resultSet.getString(2);
                        if (t.factory != null)
                            t.factory = t.factory.trim();
                        t.line = resultSet.getString(3);
                        if (t.line != null)
                            t.line = t.line.trim();
                        t.boxQty = resultSet.getInt(4);
                        t.appointmentKey = resultSet.getString(5);
                        return t;
                    }
                }
        );

    }

    public List<TrolleyOrder> getTrolleyOrders() {
        return jdbc.query(
                sqlTrolleyOrders,
                new RowMapper<TrolleyOrder>() {
                    @Override
                    public TrolleyOrder mapRow(ResultSet resultSet, int i) throws SQLException {
                        TrolleyOrder to = new TrolleyOrder();
                        to.trolleyId = resultSet.getString(1);
                        to.orderKey = resultSet.getString(2);
                        to.orderStatus = resultSet.getString(3);
                        to.putawayZone = resultSet.getString(4);
                        return to;
                    }
                });
    }

    public List<Appointment> getAppointments() {
        return jdbc.query(
                sqlAppointments,
                new RowMapper<Appointment>() {
                    @Override
                    public Appointment mapRow(ResultSet resultSet, int i) throws SQLException {
                        Appointment a = new Appointment();
                        a.key = resultSet.getString(1);
                        a.factory = resultSet.getString(2);
                        if (a.factory != null)
                            a.factory = a.factory.trim();
                        a.line = resultSet.getString(3);
                        if (a.line != null)
                            a.line = a.line.trim();
                        Date start = resultSet.getTimestamp(4);
                        a.start = start == null ? null : start.getTime();
                        return a;
                    }
                });
    }

}

