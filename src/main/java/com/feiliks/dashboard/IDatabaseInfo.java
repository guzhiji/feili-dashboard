package com.feiliks.dashboard;


public interface IDatabaseInfo {
    Long getId();
    String getName();
    String getDriver();
    String getUri();
    String getUser();
    String getPass();
}
