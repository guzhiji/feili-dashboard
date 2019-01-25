package com.feiliks.dashboard;

import javax.sql.DataSource;


public interface IDbConnManager {
    DataSource getDatabase(IDatabaseInfo db);
    DataSource updateDbInfo(IDatabaseInfo db);
    void removeDb(IDatabaseInfo db);
    void removeDb(long id);
}
