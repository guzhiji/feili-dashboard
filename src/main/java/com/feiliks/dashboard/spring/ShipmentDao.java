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

    public final static String PERFMON_TROLLEYS = "shipment!trolleys";
    public final static String PERFMON_TROLLEY_ORDER = "shipment!trolley-order";
    public final static String PERFMON_APPOINTMENTS = "shipment!appointments";

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
        private String appointmentKey;
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

        public String getAppointmentKey() {
            return appointmentKey;
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
            String r = key + ':' + start + ':';
            if ((factory != null && !factory.isEmpty()) || (line != null && !line.isEmpty()))
                r += (factory == null ? "" : factory) + '-' + (line == null ? "" : line);
            return r;
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

    @Autowired
    private PerfMonService perfMon;

    /*
    private final static String sqlTrolleys = "select distinct" +
            "    dd.DROPID trolley_id," +
            "    o.SUSR35 factory," +
            "    o.TRADINGPARTNER line," +
            "    t.box_qty," +
            "    o.APPOINTMENTKEY appointment_key " +
            "from ORDERS o" +
            "    inner join PICKDETAIL p on p.ORDERKEY = o.ORDERKEY" +
            "    inner join LOC l on l.LOC = p.LOC and l.PUTAWAYZONE = 'LKSHIP'" +
            "    inner join DROPIDDETAIL dd on dd.CHILDID = p.DROPID" +
            "    inner join DROPID d on d.DROPID = dd.DROPID and d.CARTONTYPE = 'TROLLEY'" +
            "    left join (" +
            "        select " +
            "            dd.DROPID trolley_id," +
            "            count(distinct p.DROPID) box_qty" +
            "        from PICKDETAIL p" +
            "            inner join LOC l on l.LOC = p.LOC and l.PUTAWAYZONE = 'LKSHIP'" +
            "            inner join DROPIDDETAIL dd on dd.CHILDID = p.DROPID" +
            "                inner join DROPID d on d.DROPID = dd.DROPID and d.CARTONTYPE = 'TROLLEY'" +
            "        group by dd.DROPID" +
            "    ) t on t.trolley_id = dd.DROPID";
    */

    /**
     * List trolleys within LKSHIP, whose orders are not shipped nor cancelled.
     *
     * Detailed Requirements:
     * - Boxes are counted by trolley ids.
     * - Trolleys are not duplicated even if there're multiple orders associated
     *   with them (factories and lines are presumably consistent within the same trolley).
     * - Only trolleys in LKSHIP are counted.
     * - Associated orders should not be shipped nor cancelled
     *   (in LKSHIP orders naturally shouldn't be shipped).
     * - Extra fields in orders: factory, line, and appointment_key.
     */
    private final static String sqlTrolleys = "select" +
            "    t.trolley_id," +
            "    max(o.SUSR35) factory," +
            "    max(o.TRADINGPARTNER) line," +
            "    t.box_qty," +
            "    max(o.APPOINTMENTKEY) appointment_key " +
            "from (" +
            "    select" +
            "        d.DROPID trolley_id," +
            "        count(distinct dd.CHILDID) box_qty" +
            "    from DROPID d" +
            "        inner join DROPIDDETAIL dd on dd.DROPID = d.DROPID" +
            "    where d.CARTONTYPE = 'TROLLEY' and d.DROPLOC = 'LKSHIP'" +
            "    group by d.DROPID) t " +
            "inner join DROPIDDETAIL dd on dd.DROPID = t.trolley_id " +
            "inner join PICKDETAIL p on p.DROPID = dd.CHILDID " +
            "inner join ORDERS o on o.ORDERKEY = p.ORDERKEY and o.STATUS not in ('98', '99', '95') " +
            "inner join STORER s ON s.STORERKEY = o.STORERKEY and s.TYPE = '1' and s.SUSR2 like 'CQ%' " + // CQ2 -> CQ%
            "group by t.trolley_id, t.box_qty";

    /**
     * List trolley-order pairs in CQ2 whose order status is not shipped nor cancelled.
     *
     * Detailed Requirements:
     * - Appointment keys are needed to test if status is APPOINTMENT (non-empty).
     * - Locations and order status are needed to test if status is WAITING (LKSHIP and 55-PICKED).
     * - Area: CQ2.
     * - Order status: not shipped nor cancelled.
     * - Trolleys moved out of LKSHIP are presumably shipped - they shouldn't
     *   be back to status OTHERS when leaving LKSHIP.
     */
    private final static String sqlTrolleyOrders = "select " +
            "    d.DROPID trolley_id," +
            "    o.ORDERKEY order_key," +
            "    o.STATUS order_status," +
            "    trim(o.APPOINTMENTKEY) appt_key," +
            // "    l.PUTAWAYZONE " +
            "    d.DROPLOC " +
            "from ORDERS o" +
            "    inner join STORER s on s.STORERKEY = o.STORERKEY and s.TYPE = '1' and s.SUSR2 like 'CQ%'" + // CQ2 -> CQ%
            "    inner join PICKDETAIL p on p.ORDERKEY = o.ORDERKEY" +
            // "    inner join LOC l on l.LOC = p.LOC" +
            // "    inner join AREADETAIL ad on ad.PUTAWAYZONE = l.PUTAWAYZONE and ad.AREAKEY='CQ2'" +
            "    inner join DROPIDDETAIL dd on dd.CHILDID = p.DROPID" +
            "    inner join DROPID d on d.DROPID = dd.DROPID and d.CARTONTYPE = 'TROLLEY' and d.DROPLOC in ('LKSHIP', 'LKCONS') " +
            "where" +
            "    o.STATUS not in ('98', '99', '95')";

    /**
     * List appointments open in LSHIP with factory & line info and start time.
     *
     * Detailed Requirements:
     * - Associated orders should not be shipped nor cancelled.
     * - Appointment status is not completed.
     * - Location is LKSHIP.
     * - Extra fields: factory, line, and start time of the appointment
     * - Factories and lines are presumably consistent within the same appointment.
     * - The raw start time values are not +8.
     */
    private final static String sqlAppointments = "select " +
            "    a.APPOINTMENTKEY appointment_key," +
            "    max(o.SUSR35) factory," +
            "    max(o.TRADINGPARTNER) line," +
            "    CAST((FROM_TZ(CAST(a.ADDDATE AS TIMESTAMP),'+00:00') AT TIME ZONE 'Asia/Shanghai') AS DATE) start_time " +
            "from APPOINTMENT a" +
            "    inner join ORDERS o on o.APPOINTMENTKEY = a.APPOINTMENTKEY and o.STATUS not in ('98', '99', '95')" +
            "        inner join PICKDETAIL p on p.ORDERKEY = o.ORDERKEY" +
            "        inner join DROPIDDETAIL dd on dd.CHILDID = p.DROPID" +
            "        inner join DROPID d on d.DROPID = dd.DROPID and d.DROPLOC = 'LKSHIP' " +
            // "        inner join LOC l on l.LOC = p.LOC and l.PUTAWAYZONE = 'LKSHIP' " +
            "where a.STATUS <> '5COMP' " +
            "group by a.APPOINTMENTKEY, a.ADDDATE " +
            "order by start_time";

    public List<Trolley> getTrolleys() {
        long timerStart = System.currentTimeMillis();
        try {
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
                    });
        } finally {
            perfMon.measure(PERFMON_TROLLEYS, timerStart);
        }
    }

    public List<TrolleyOrder> getTrolleyOrders() {
        long timerStart = System.currentTimeMillis();
        try {
            return jdbc.query(
                    sqlTrolleyOrders,
                    new RowMapper<TrolleyOrder>() {
                        @Override
                        public TrolleyOrder mapRow(ResultSet resultSet, int i) throws SQLException {
                            TrolleyOrder to = new TrolleyOrder();
                            to.trolleyId = resultSet.getString(1);
                            to.orderKey = resultSet.getString(2);
                            to.orderStatus = resultSet.getString(3);
                            to.appointmentKey = resultSet.getString(4);
                            to.putawayZone = resultSet.getString(5);
                            return to;
                        }
                    });
        } finally {
            perfMon.measure(PERFMON_TROLLEY_ORDER, timerStart);
        }
    }

    public List<Appointment> getAppointments() {
        long timerStart = System.currentTimeMillis();
        try {
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
        } finally {
            perfMon.measure(PERFMON_APPOINTMENTS, timerStart);
        }
    }

}

