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

    public enum Status {
        PICKED, SHIPPED, OTHER
    }

    public class OrderTrolley {
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

    private final static String sqlOrderTrolley = "select distinct " +
            "    dd.DROPID trolley_id," +
            "    o.ORDERKEY order_key," +
            "    o.STATUS status," +
            "    l.ISASRS is_asrs," +
            "    o.REQUESTEDSHIPDATE ship_time," +
            "    o.STORERKEY storer_key," +
            "    s.COMPANY storer_name," +
            "    o.SUSR35 consignee_key," +
            "    c.COMPANY consignee_name," +
            "    o.CONSIGNEEKEY factory," +
            "    o.TRADINGPARTNER line," +
            "    o.EDITDATE op_time " +
            "from ORDERS o" +
            "    inner join PICKDETAIL p on p.ORDERKEY = o.ORDERKEY" +
            "    inner join LOC l on l.LOC = p.LOC" +
            "        inner join AREADETAIL ad on ad.PUTAWAYZONE = l.PUTAWAYZONE and ad.AREAKEY = 'CQ2'" +
            "    inner join DROPIDDETAIL dd on dd.CHILDID = p.DROPID" +
            "        inner join DROPID d on d.DROPID = dd.DROPID and d.DROPIDTYPE = '10'" +
            "    left join STORER s on s.STORERKEY = o.STORERKEY and s.TYPE = '1'" +
            "    left join STORER c on c.STORERKEY = o.SUSR35 and c.TYPE = '10' " +
            "where" +
            "    o.STATUS not in ('98', '99') and" +
            "        o.REQUESTEDSHIPDATE >= trunc(sysdate) and" +
            "        o.REQUESTEDSHIPDATE < trunc(sysdate) + 1";

    public List<OrderTrolley> getOrderTrolley() {
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

        Map<String, List<OrderTrolley>> asrsMap = new HashMap<>();
        for (OrderTrolley ot : result) {

            ot.status = convertStatus(ot.getStatus());

            if (!asrsMap.containsKey(ot.getOrderKey())) {
                List<OrderTrolley> ots = new LinkedList<>();
                ots.add(ot);
                asrsMap.put(ot.getOrderKey(), ots);
            } else {
                asrsMap.get(ot.getOrderKey()).add(ot);
            }

        }

        for (List<OrderTrolley> ots : asrsMap.values()) {
            boolean asrs = ots.get(0).isAsrs();
            for (OrderTrolley ot : ots) {
                if (asrs != ot.isAsrs()) {
                    for (OrderTrolley ot2 : ots)
                        ot2.isToCombine = true;
                    break;
                }
            }
        }

        return result;
    }

    private String convertStatus(String s) {
        if (s != null) s = s.trim();
        if ("55".equals(s))
            return Status.PICKED.name();
        else if ("95".equals(s))
            return Status.SHIPPED.name();
        else
            return Status.OTHER.name();
    }

}
