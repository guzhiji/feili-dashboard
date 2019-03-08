package com.feiliks.dashboard.spring;

import com.feiliks.dashboard.spring.impl.MonitorInfo;
import com.feiliks.dashboard.spring.entities.MonitorEntity;
import org.junit.Test;

import static org.junit.Assert.*;

public class MonitorDataTest {

    @Test
    public void testGetConfig() {
        MonitorEntity entity = new MonitorEntity();
        entity.setConfigData("{\"a\": 1}");
        MonitorInfo data = new MonitorInfo(entity);
        assertEquals(1, data.readConfig("a"));
        assertNull(data.readConfig("b"));
    }

}
