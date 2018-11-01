package com.feiliks.dashboard.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ShipmentDao {
    @Autowired
    private JdbcTemplate jdbc;

}
