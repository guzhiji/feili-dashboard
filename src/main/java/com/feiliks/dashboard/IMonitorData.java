package com.feiliks.dashboard;

public interface IMonitorData {
    Long getId();
    String getName();
    String getJavaClass();
    String getArgs();
    long getExecRate();
}
