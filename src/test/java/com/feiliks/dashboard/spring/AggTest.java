package com.feiliks.dashboard.spring;

import org.junit.Assert;
import org.junit.Test;

public class AggTest {

    private PerfMonitor.Agg prepare(long[] data) {
        PerfMonitor.Agg a = new PerfMonitor.Agg(
                System.currentTimeMillis());
        for (long value : data)
            a.add(value);
        return a;
    }

    @Test
    public void testMaxMin() {
        PerfMonitor.Agg a = prepare(new long[] {2, 4, 1, 5, 3});
        Assert.assertEquals(1L, a.min().longValue());
        Assert.assertEquals(5L, a.max().longValue());
    }

    @Test
    public void testMeanMedianOdd() {
        PerfMonitor.Agg a = prepare(new long[] {3, 7, 1, 9, 5});
        Assert.assertEquals(5F, a.mean(), 0.0);
        Assert.assertEquals(5F, a.percentile(50), 0.0);
    }

    @Test
    public void testMeanMedianOdd2() {
        PerfMonitor.Agg a = prepare(new long[] {3, 7, 6, 9, 5});
        Assert.assertEquals(6F, a.mean(), 0.0);
        Assert.assertEquals(6F, a.percentile(50), 0.0);
    }

    @Test
    public void testMeanMedianOdd3() {
        PerfMonitor.Agg a = prepare(new long[] {3, 4, 1, 9, 5});
        Assert.assertEquals(4.4F, a.mean(), 0.0);
        Assert.assertEquals(4F, a.percentile(50), 0.0);
    }

    @Test
    public void testMeanMedianEven() {
        PerfMonitor.Agg a = prepare(new long[] {3, 7, 1, 9, 5, 10});
        Assert.assertEquals(5.833F, a.mean(), 0.001);
        Assert.assertEquals(6F, a.percentile(50), 0.0);
    }

    @Test
    public void testMeanMedianEven2() {
        PerfMonitor.Agg a = prepare(new long[] {3, 7, 6, 9, 5, 10});
        Assert.assertEquals(6.666F, a.mean(), 0.001);
        Assert.assertEquals(6.5F, a.percentile(50), 0.0);
    }

    @Test
    public void testMeanMedianEven3() {
        PerfMonitor.Agg a = prepare(new long[] {3, 4, 1, 9, 5, 0});
        Assert.assertEquals(3.666F, a.mean(), 0.001);
        Assert.assertEquals(3.5F, a.percentile(50), 0.0);
    }

}
