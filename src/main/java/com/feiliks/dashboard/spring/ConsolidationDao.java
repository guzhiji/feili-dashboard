package com.feiliks.dashboard.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class ConsolidationDao {

    public enum Status {
        PICKED, SHIPPED, OTHER
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
    }

    public static class StatusCount {
        private String status;
        private long count;
        public String getStatus() {
            return status;
        }
        public long getCount() {
            return count;
        }
    }

    public static class TimelyStatusCount {
        private long time;
        private String status;
        private long count;
        public long getTime() {
            return time;
        }
        public String getStatus() {
            return status;
        }
        public long getCount() {
            return count;
        }
    }

    @Autowired
    private JdbcTemplate jdbc;

    private final static String sqlTableSub = "select distinct " +
            "    dd.DROPID trolley_id," +
            "    o.ORDERKEY order_key," +
            "    o.STATUS status," +
            "    l.ISASRS is_asrs," +
            "    o.REQUESTEDSHIPDATE ship_date," +
            "    o.STORERKEY storer_key," +
            "    s.COMPANY storer_name," +
            "    o.SUSR35 consignee_key," +
            "    c.COMPANY consignee_name," +
            "    o.CONSIGNEEKEY factory," +
            "    o.TRADINGPARTNER line " +
            "from ORDERS o" +
            "    inner join PICKDETAIL p on p.ORDERKEY = o.ORDERKEY" +
            "    inner join LOC l on l.LOC = p.LOC" +
            "        inner join AREADETAIL ad on ad.PUTAWAYZONE = l.PUTAWAYZONE and ad.AREAKEY = 'CQ2'" +
            "    inner join DROPIDDETAIL dd on dd.CHILDID = p.DROPID" +
            "        inner join DROPID d on d.DROPID = dd.DROPID and d.DROPIDTYPE = '10'" +
            "    left join STORER s on s.STORERKEY = o.STORERKEY and s.TYPE = '1'" +
            "    left join STORER c on c.STORERKEY = o.SUSR35 and c.TYPE = '10' " +
            "where" +
            "    o.STATUS not in ('98', '99', '95') and" +
            "        o.REQUESTEDSHIPDATE >= trunc(sysdate) and" +
            "        o.REQUESTEDSHIPDATE < trunc(sysdate) + 1";

    private final static String sqlTable = "select t.*, t3.order_key to_combine " +
            "from (" + sqlTableSub + ") t " +
            "left join (select distinct t2.order_key " +
            "    from (" + sqlTableSub + ") t2 " +
            "    group by t2.order_key having count(distinct t2.is_asrs) > 1" +
            ") t3 on t3.order_key = t.order_key";

    private final static String sqlLine = "select " +
            "    o.STATUS status," +
            "    to_char(o.EDITDATE, 'YYYY-MM-DD HH24') op_time," +
            "    count(distinct o.ORDERKEY) order_count " +
            "from ORDERS o" +
            "    inner join PICKDETAIL p on p.ORDERKEY = o.ORDERKEY" +
            "    inner join LOC l on l.LOC = p.LOC" +
            "        inner join AREADETAIL ad on ad.PUTAWAYZONE = l.PUTAWAYZONE and ad.AREAKEY = 'CQ2'" +
            "    inner join DROPIDDETAIL dd on dd.CHILDID = p.DROPID" +
            "        inner join DROPID d on d.DROPID = dd.DROPID and d.DROPIDTYPE = '10' " +
            "where" +
            "    o.STATUS in ('95', '55') and" +
            "        o.REQUESTEDSHIPDATE >= trunc(sysdate) and" +
            "        o.REQUESTEDSHIPDATE < trunc(sysdate) + 1 " +
            "group by o.STATUS, to_char(o.EDITDATE, 'YYYY-MM-DD HH24')";

    private final static String sqlPie = "select" +
            "    o.STATUS status," +
            "    count(distinct o.ORDERKEY) order_count " +
            "from ORDERS o" +
            "    inner join PICKDETAIL p on p.ORDERKEY = o.ORDERKEY" +
            "    inner join LOC l on l.LOC = p.LOC" +
            "        inner join AREADETAIL ad on ad.PUTAWAYZONE = l.PUTAWAYZONE and ad.AREAKEY = 'CQ2'" +
            "    inner join DROPIDDETAIL dd on dd.CHILDID = p.DROPID" +
            "        inner join DROPID d on d.DROPID = dd.DROPID and d.DROPIDTYPE = '10' " +
            "where" +
            "    o.STATUS not in ('98', '99') and" +
            "        o.REQUESTEDSHIPDATE >= trunc(sysdate) and" +
            "        o.REQUESTEDSHIPDATE < trunc(sysdate) + 1 " +
            "group by o.STATUS";

    public List<OrderTrolley> getTable() {
        return jdbc.query(sqlTable, new RowMapper<OrderTrolley>() {
            @Override
            public OrderTrolley mapRow(ResultSet resultSet, int i) throws SQLException {
                OrderTrolley o = new OrderTrolley();
                o.trolleyId = resultSet.getString(1);
                o.orderKey = resultSet.getString(2);
                o.status = convertStatus(resultSet.getString(3));
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
                o.isToCombine = resultSet.getString(12) != null;
                return o;
            }
        });
    }

    public List<StatusCount> getPie() {
        return jdbc.query(sqlPie, new RowMapper<StatusCount>() {
            @Override
            public StatusCount mapRow(ResultSet resultSet, int i) throws SQLException {
                StatusCount s = new StatusCount();
                s.status = convertStatus(resultSet.getString(1));
                s.count = resultSet.getLong(2);
                return s;
            }
        });
    }

    public List<TimelyStatusCount> getLine() {
        return jdbc.query(sqlLine, new RowMapper<TimelyStatusCount>() {
            @Override
            public TimelyStatusCount mapRow(ResultSet resultSet, int i) throws SQLException {
                TimelyStatusCount c = new TimelyStatusCount();
                c.status = convertStatus(resultSet.getString(1));
                c.time = convertTime(resultSet.getString(2));
                c.count = resultSet.getLong(3);
                return c;
            }
        });
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

    private final static SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy.MM.dd HH");

    private long convertTime(String time) {
        try {
            return timeFormatter.parse(time).getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

}
