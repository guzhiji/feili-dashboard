package com.feiliks.dashboard.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Date;

@Repository
public class BaseTimeDao {

    @Autowired
    private JdbcTemplate jdbc;

    private final static String sqlTime = "select sysdate from dual";

    public Date getDBTime() {
        return jdbc.queryForObject(sqlTime, Timestamp.class);
    }

}
