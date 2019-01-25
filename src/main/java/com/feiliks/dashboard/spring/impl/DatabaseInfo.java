package com.feiliks.dashboard.spring.impl;

import com.feiliks.dashboard.IDatabaseInfo;
import com.feiliks.dashboard.spring.entities.DatabaseEntity;

public class DatabaseInfo implements IDatabaseInfo {

    private Long id;
    private String name;
    private String driver;
    private String uri;
    private String user;
    private String pass;

    public DatabaseInfo(DatabaseEntity entity) {
        id = entity.getId();
        name = entity.getName();
        driver = entity.getDbDriver();
        uri = entity.getDbUri();
        user = entity.getDbUser();
        pass = entity.getDbPass();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDriver() {
        return driver;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getPass() {
        return pass;
    }

}
