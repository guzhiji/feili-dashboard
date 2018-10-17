package com.feiliks.dashboard.spring;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class SysInfoTest {

    @Test
    public void testExtractKeyNum() throws IOException {
        Map<String, Long> m = SysInfo.extractKeyNum("/proc/meminfo");
        Assert.assertTrue(m.containsKey("MemTotal"));
        Assert.assertTrue(m.containsKey("MemFree"));
        Assert.assertTrue(m.containsKey("MemAvailable"));
    }

    @Test
    public void testExtractCPUs() throws IOException {
        Map<String, Long[]> m = SysInfo.extractCPUs("/proc/stat");
        Assert.assertTrue(!m.isEmpty());
    }

}
