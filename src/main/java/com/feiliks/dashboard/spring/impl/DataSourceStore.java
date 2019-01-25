package com.feiliks.dashboard.spring.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceStore {

    private final Map<String, String> data = new ConcurrentHashMap<>();

    public void clear(String internalName) {
        data.put(internalName, null);
    }

    public void store(String internalName, Object result)
            throws JsonProcessingException {
        String value = new ObjectMapper().writeValueAsString(result);
        data.put(internalName, value);
    }

    public String retrieveDataSourceAsJson(String internalName) {
        String result = data.get(internalName);
        if (result == null)
            return "null";
        return result;
    }

}
