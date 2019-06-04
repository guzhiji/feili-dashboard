package com.feiliks.dashboard.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class ConsolidationDao {

    public final static String PERFMON_KEY = "consolidation!order-trolley";

    public enum Status {
        PICKED, SHIPPED, OTHER;

        private static List<Status> listWithoutOther = null;
        public static Collection<Status> valuesExceptOther() {
            if (listWithoutOther == null) {
                List<Status> list = new ArrayList<>();
                for (Status status : values()) {
                    if (!status.equals(Status.OTHER))
                        list.add(status);
                }
                listWithoutOther = Collections.unmodifiableList(list);
            }
            return listWithoutOther;
        }

    }

    public static class OrderTrolley {
        private String trolleyId;
        private String orderKey;
        private String status;
        private boolean isAsrs;
        private boolean isToCombine;
        private long shipDate;
        private String storer;
        private String storerName;
        private String consignee;
        private String consigneeName;
        private String factory;
        private String line;
        private Date opTime;

        public String getTrolleyId() {
            return trolleyId;
        }

        public String getOrderKey() {
            return orderKey;
        }

        public String getStatus() {
            return status;
        }

        public boolean isAsrs() {
            return isAsrs;
        }

        public boolean isToCombine() {
            return isToCombine;
        }

        public long getShipDate() {
            return shipDate;
        }

        public String getStorer() {
            return storer;
        }

        public String getStorerName() {
            return storerName;
        }

        public String getConsignee() {
            return consignee;
        }

        public String getConsigneeName() {
            return consigneeName;
        }

        public String getFactory() {
            return factory;
        }

        public String getLine() {
            return line;
        }

        public Date getOpTime() {
            return opTime;
        }
    }

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private PerfMonService perfMon;

    /**
     * List order-trolley pairs that are requested to ship today in CQ2.
     *
     * Detailed Requirements:
     * - Order status is not cancelled.
     * - Location: in CQ2
     * - Orders are requested to ship today.
     * - Requested shipping time is in UTC.
     * - ISASRS is needed to test whether an order should 'combine'.
     * - Op time is used to recover historical data partially on startup
     *   (non-latest status is lost if not in memory;
     *   edit time is not equivalent to op time).
     */
    private final static String sqlOrderTrolley = "select distinct " +
            "    dd.DROPID trolley_id," +
            "    o.ORDERKEY order_key," +
            "    o.STATUS status," +
            "    l.ISASRS is_asrs," +
            "    CAST((FROM_TZ(CAST(o.REQUESTEDSHIPDATE AS TIMESTAMP),'+00:00') AT TIME ZONE 'Asia/Shanghai') AS DATE) ship_time," +
            "    o.STORERKEY storer_key," +
            "    s.COMPANY storer_name," +
            "    o.SUSR35 consignee_key," +
            "    c.COMPANY consignee_name," +
            "    o.SUSR35 factory," +
            "    o.TRADINGPARTNER line," +
            "    CAST((FROM_TZ(CAST(o.EDITDATE AS TIMESTAMP),'+00:00') AT TIME ZONE 'Asia/Shanghai') AS DATE) op_time " +
            "from ORDERS o" +
            "    inner join PICKDETAIL p on p.ORDERKEY = o.ORDERKEY" +
            "    inner join LOC l on l.LOC = p.LOC" +
            // "        inner join AREADETAIL ad on ad.PUTAWAYZONE = l.PUTAWAYZONE and ad.AREAKEY = 'CQ2'" +
            "    inner join DROPIDDETAIL dd on dd.CHILDID = p.DROPID" +
            "        inner join DROPID d on d.DROPID = dd.DROPID and d.CARTONTYPE = 'TROLLEY'" +
            "    inner join STORER s on s.STORERKEY = o.STORERKEY and s.TYPE = '1' and s.SUSR2 = 'CQ2'" +
            "    left join STORER c on c.STORERKEY = o.SUSR35 and c.TYPE = '10' " +
            "where" +
            "    o.STATUS not in ('98', '99') and" +
            "        o.REQUESTEDSHIPDATE >= trunc(SYS_EXTRACT_UTC(SYSTIMESTAMP)) and" +
            "        o.REQUESTEDSHIPDATE < trunc(SYS_EXTRACT_UTC(SYSTIMESTAMP)) + 1";

    public List<OrderTrolley> getOrderTrolley() {
        long timerStart = System.currentTimeMillis();
        try {
            List<OrderTrolley> result = jdbc.query(
                    sqlOrderTrolley,
                    new RowMapper<OrderTrolley>() {
                        @Override
                        public OrderTrolley mapRow(ResultSet resultSet, int i) throws SQLException {
                            OrderTrolley o = new OrderTrolley();
                            o.trolleyId = resultSet.getString(1);
                            o.orderKey = resultSet.getString(2);
                            o.status = resultSet.getString(3);
                            o.isToCombine = false;
                            o.isAsrs = !"0".equals(resultSet.getString(4));
                            Date shipDate = resultSet.getTimestamp(5);
                            o.shipDate = shipDate == null ? 0L : shipDate.getTime();
                            o.storer = resultSet.getString(6);
                            o.storerName = resultSet.getString(7);
                            if (o.storerName != null)
                                o.storerName = o.storerName.trim();
                            o.consignee = resultSet.getString(8);
                            o.consigneeName = resultSet.getString(9);
                            if (o.consigneeName != null)
                                o.consigneeName = o.consigneeName.trim();
                            o.factory = resultSet.getString(10);
                            if (o.factory != null)
                                o.factory = o.factory.trim();
                            o.line = resultSet.getString(11);
                            if (o.line != null)
                                o.line = o.line.trim();
                            o.opTime = resultSet.getTimestamp(12);
                            return o;
                        }
                    });

            // group by order key
            Map<String, List<OrderTrolley>> asrsMap = new HashMap<>();
            for (OrderTrolley ot : result) {
                if (!asrsMap.containsKey(ot.getOrderKey())) {
                    List<OrderTrolley> ots = new LinkedList<>();
                    ots.add(ot);
                    asrsMap.put(ot.getOrderKey(), ots);
                } else {
                    asrsMap.get(ot.getOrderKey()).add(ot);
                }
            }
            // set isToCombine
            for (List<OrderTrolley> ots : asrsMap.values()) {
                boolean asrs = ots.get(0).isAsrs();
                for (OrderTrolley ot : ots) {
                    if (asrs != ot.isAsrs()) { // isAsrs not consistent
                        // update all records relevant to the current order
                        for (OrderTrolley ot2 : ots)
                            ot2.isToCombine = true;
                        break;
                    }
                }
            }
            // distinct result by order key and trolley id
            List<OrderTrolley> distinctList = new ArrayList<>(result.size());
            Set<String> otPairs = new HashSet<>();
            for (OrderTrolley ot : result) {
                String otp = ot.getOrderKey() + "-" + ot.getTrolleyId();
                if (!otPairs.contains(otp)) {
                    ot.status = convertStatus(ot.getStatus());
                    distinctList.add(ot);
                    otPairs.add(otp);
                }
            }

            return distinctList;
        } finally {
            perfMon.measure(PERFMON_KEY, timerStart);
        }
    }

    private String convertStatus(String s) {
        if (s != null) {
            try {
                int status = Integer.parseInt(s.trim());
                if (status >= 95)
                    return Status.SHIPPED.name();
                if (status >= 55)
                    return Status.PICKED.name();
            } catch (NumberFormatException ignored) {
            }
        }
        return Status.OTHER.name();
    }

}
