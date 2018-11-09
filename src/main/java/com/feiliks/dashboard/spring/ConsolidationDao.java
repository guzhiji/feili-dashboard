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
    public static class PickDetail {
        private String orderKey;
        private String dropId;
        private String status;
        private boolean isAsrs;

        public String getOrderKey() {
            return orderKey;
        }

        public String getDropId() {
            return dropId;
        }

        public String getStatus() {
            return status;
        }

        public boolean isAsrs() {
            return isAsrs;
        }

    }

    public class OrderTrolley {
        private String trolleyId;
        private String orderKey;
        private String status;
        private boolean isAsrs;
        private boolean isToCombine;
        private Date shipDate;
        private String storer;
        private String storerName;
        private String consignee;
        private String consigneeName;
        private String factory;
        private String line;

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

        public Date getShipDate() {
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
    }

    @Autowired
    private JdbcTemplate jdbc;

    private final static String sqlPickDetail = "select p.ORDERKEY, p.DROPID, p.STATUS, l.ISASRS from LOC l " +
            "inner join PICKDETAIL p on p.LOC = l.LOC " +
            "where l.PUTAWAYZONE = ? " +
            "union all " +
            "select p.ORDERKEY, p.DROPID, p.STATUS, l.ISASRS from AREADETAIL ad " +
            "inner join LOC l on l.PUTAWAYZONE = ad.PUTAWAYZONE " +
            "inner join PICKDETAIL p on p.LOC = l.LOC " +
            "where ad.AREAKEY = ?";

    private final static String sqlOrderTrolley = "select " +
            "dd.DROPID trolley_id, " +
            "t.ORDERKEY order_key, " +
            "t.STATUS status, " +
            "t.ISASRS is_asrs, " +
            "o.REQUESTEDSHIPDATE ship_date, " +
            "o.STORERKEY storer, " +
            "s.COMPANY storer_name, " +
            "o.SUSR35 consignee, " +
            "c.COMPANY consignee_name, " +
            "o.CONSIGNEEKEY factory, " +
            "o.TRADINGPARTNER line " +
            "from (" + sqlPickDetail + ") t " +
            "inner join DROPIDDETAIL dd on dd.CHILDID = t.DROPID " +
            "inner join DROPID d on d.DROPID = dd.DROPID and d.DROPIDTYPE = ? " + 
            "left join ORDERS o on o.ORDERKEY = t.ORDERKEY " +
            "left join STORER s on s.STORERKEY = o.STORERKEY and s.TYPE = '1' " +
            "left join STORER c on c.STORERKEY = o.SUSR35 and c.TYPE = '10'";

    public List<PickDetail> getPickDetails(String putAwayZone, String areaKey) {
        return jdbc.query(
                sqlPickDetail,
                new Object[] {putAwayZone, areaKey},
                new RowMapper<PickDetail>() {
                    @Override
                    public PickDetail mapRow(ResultSet resultSet, int i) throws SQLException {
                        PickDetail p = new PickDetail();
                        p.orderKey = resultSet.getString(1);
                        p.dropId = resultSet.getString(2);
                        p.status = resultSet.getString(3);
                        p.isAsrs = !"0".equals(resultSet.getString(4));
                        return p;
                    }
                });
    }

    public List<PickDetail> getPickDetails() {
        return getPickDetails("AUTO", "CQ2");
    }

    public List<OrderTrolley> getOrderTrolley(String putAwayZone, String areaKey) {
        List<OrderTrolley> result = jdbc.query(
                sqlOrderTrolley,
                new Object[]{putAwayZone, areaKey, "10"},
                new RowMapper<OrderTrolley>() {
                    @Override
                    public OrderTrolley mapRow(ResultSet resultSet, int i) throws SQLException {
                        OrderTrolley o = new OrderTrolley();
                        o.trolleyId = resultSet.getString(1);
                        o.orderKey = resultSet.getString(2);
                        o.status = resultSet.getString(3);
                        o.isToCombine = false;
                        o.isAsrs = !"0".equals(resultSet.getString(4));
                        o.shipDate = resultSet.getDate(5);
                        o.storer = resultSet.getString(6);
                        o.storerName = resultSet.getString(7);
                        o.consignee = resultSet.getString(8);
                        o.consigneeName = resultSet.getString(9);
                        o.factory = resultSet.getString(10);
                        o.line = resultSet.getString(11);
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
        if ("5".equals(s) || "6".equals(s))
            return Status.PICKED.name();
        else if ("9".equals(s))
            return Status.SHIPPED.name();
        else
            return Status.OTHER.name();
    }

    public List<OrderTrolley> getOrderTrolley() {
        return getOrderTrolley("AUTO", "CQ2");
    }

}
