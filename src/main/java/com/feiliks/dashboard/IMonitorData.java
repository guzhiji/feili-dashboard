package com.feiliks.dashboard;

public interface IMonitorData {
    Long getId();
    String getName();
    String getJavaClass();
    long getExecRate();
    IDatabaseInfo getDatabaseInfo();
    Object readConfig(String name);
}
