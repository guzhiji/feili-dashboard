package com.feiliks.dashboard.spring.dto;

import com.feiliks.dashboard.spring.entities.DatabaseEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class DatabaseFormDto {

    private Long id;

    @NotNull(message = "database-name-empty")
    @NotBlank(message = "database-name-empty")
    @Size(max = 32, message = "database-name-toolong")
    private String name;

    @NotNull(message = "database-driver-empty")
    @NotBlank(message = "database-driver-empty")
    private String dbDriver;

    @NotNull(message = "database-uri-empty")
    @NotBlank(message = "database-uri-empty")
    private String dbUri;

    @NotNull(message = "database-user-empty")
    @NotBlank(message = "database-user-empty")
    private String dbUser;

    private String dbPass;

    public DatabaseEntity toEntity() {
        DatabaseEntity entity = new DatabaseEntity();
        toEntity(entity);
        return entity;
    }

    public void toEntity(DatabaseEntity entity) {
        entity.setId(getId());
        entity.setName(getName());
        entity.setDbDriver(getDbDriver());
        entity.setDbUri(getDbUri());
        entity.setDbUser(getDbUser());
        entity.setDbPass(getDbPass());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }

    public String getDbUri() {
        return dbUri;
    }

    public void setDbUri(String dbUri) {
        this.dbUri = dbUri;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPass() {
        return dbPass;
    }

    public void setDbPass(String dbPass) {
        this.dbPass = dbPass;
    }
}
