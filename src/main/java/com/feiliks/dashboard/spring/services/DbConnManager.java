package com.feiliks.dashboard.spring.services;

import com.feiliks.dashboard.IDatabaseInfo;
import com.feiliks.dashboard.IDbConnManager;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class DbConnManager implements IDbConnManager {

    private final Map<Long, DataSource> dataSources = new ConcurrentHashMap<>();

    private DataSource saveDbInfo(IDatabaseInfo db) {
        DataSource ds = DataSourceBuilder.create()
                .driverClassName(db.getDriver())
                .url(db.getUri())
                .username(db.getUser())
                .password(db.getPass())
                .build();
        dataSources.put(db.getId(), ds);
        return ds;
    }

    @Override
    public DataSource getDatabase(IDatabaseInfo db) {
        if (dataSources.containsKey(db.getId()))
            return dataSources.get(db.getId());
        return saveDbInfo(db);
    }

    @Override
    public DataSource updateDbInfo(IDatabaseInfo db) {
        if (dataSources.containsKey(db.getId()))
            return saveDbInfo(db);
        return null;
    }

    @Override
    public void removeDb(IDatabaseInfo db) {
        dataSources.remove(db.getId());
    }

    @Override
    public void removeDb(long id) {
        dataSources.remove(id);
    }

}
