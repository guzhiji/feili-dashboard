package com.feiliks.dashboard.spring;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class ShipmentUtilsTests {

    private boolean isWaiting(List<ShipmentDao.TrolleyOrder> trolleyOrders, String trolleyId) {
        Map<String, Set<ShipmentDao.TrolleyOrder>> groupByTrolley = new HashMap<>();
        Map<String, Set<ShipmentDao.TrolleyOrder>> groupByOrder = new HashMap<>();
        ShipmentUtils.groupTrolleyOrderPairs(trolleyOrders, groupByTrolley, groupByOrder);
        return ShipmentUtils.isWaiting(groupByTrolley, groupByOrder, new HashSet<String>(), trolleyId);
    }

    @Test
    public void testIsWaiting_A_OK() {
        List<ShipmentDao.TrolleyOrder> trolleyOrders = Arrays.asList(
                new ShipmentDao.TrolleyOrder(
                        "a", "1", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "a", "2", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "b", "3", "52", "LKSHIP")
        );
        Assert.assertTrue(isWaiting(trolleyOrders, "a"));
    }

    @Test
    public void testIsWaiting_A_NotPicked() {
        List<ShipmentDao.TrolleyOrder> trolleyOrders = Arrays.asList(
                new ShipmentDao.TrolleyOrder(
                        "a", "1", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "a", "2", "52", "LKSHIP")
        );
        Assert.assertFalse(isWaiting(trolleyOrders, "a"));
    }

    @Test
    public void testIsWaiting_A_AfterPicked() {
        List<ShipmentDao.TrolleyOrder> trolleyOrders = Arrays.asList(
                new ShipmentDao.TrolleyOrder(
                        "a", "1", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "a", "2", "61", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "a", "3", "68", "LKSHIP")
        );
        Assert.assertTrue(isWaiting(trolleyOrders, "a"));
    }

    @Test
    public void testIsWaiting_A_NotInLKSHIP() {
        List<ShipmentDao.TrolleyOrder> trolleyOrders = Arrays.asList(
                new ShipmentDao.TrolleyOrder(
                        "a", "1", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "a", "2", "55", "LKCONS")
        );
        Assert.assertFalse(isWaiting(trolleyOrders, "a"));
    }

    @Test
    public void testIsWaiting_B_OK() {
        List<ShipmentDao.TrolleyOrder> trolleyOrders = Arrays.asList(
                new ShipmentDao.TrolleyOrder(
                        "a", "1", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "a", "2", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "b", "1", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "b", "2", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "b", "3", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "c", "4", "52", "LKSHIP")
        );
        Assert.assertTrue(isWaiting(trolleyOrders, "a"));
    }

    @Test
    public void testIsWaiting_B_NotPicked() {
        List<ShipmentDao.TrolleyOrder> trolleyOrders = Arrays.asList(
                new ShipmentDao.TrolleyOrder(
                        "a", "1", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "a", "2", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "b", "1", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "b", "3", "52", "LKSHIP")
        );
        Assert.assertFalse(isWaiting(trolleyOrders, "a"));
    }

    @Test
    public void testIsWaiting_B_AfterPicked() {
        List<ShipmentDao.TrolleyOrder> trolleyOrders = Arrays.asList(
                new ShipmentDao.TrolleyOrder(
                        "a", "1", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "a", "2", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "b", "1", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "b", "3", "61", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "b", "3", "68", "LKSHIP")
        );
        Assert.assertTrue(isWaiting(trolleyOrders, "a"));
    }

    @Test
    public void testIsWaiting_B_NotInLKSHIP() {
        List<ShipmentDao.TrolleyOrder> trolleyOrders = Arrays.asList(
                new ShipmentDao.TrolleyOrder(
                        "a", "1", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "a", "2", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "b", "1", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "b", "3", "55", "LKCONS")
        );
        Assert.assertFalse(isWaiting(trolleyOrders, "a"));
    }

    @Test
    public void testIsWaiting_C_OK() {
        List<ShipmentDao.TrolleyOrder> trolleyOrders = Arrays.asList(
                new ShipmentDao.TrolleyOrder(
                        "a", "1", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "a", "2", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "b", "1", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "b", "3", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "c", "3", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "c", "4", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "d", "5", "52", "LKSHIP")
        );
        Assert.assertTrue(isWaiting(trolleyOrders, "a"));
    }

    @Test
    public void testIsWaiting_C_NotPicked() {
        List<ShipmentDao.TrolleyOrder> trolleyOrders = Arrays.asList(
                new ShipmentDao.TrolleyOrder(
                        "a", "1", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "a", "2", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "b", "1", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "b", "3", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "c", "3", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "c", "4", "52", "LKSHIP")
        );
        Assert.assertFalse(isWaiting(trolleyOrders, "a"));
    }

    @Test
    public void testIsWaiting_C_AfterPicked() {
        List<ShipmentDao.TrolleyOrder> trolleyOrders = Arrays.asList(
                new ShipmentDao.TrolleyOrder(
                        "a", "1", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "a", "2", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "b", "1", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "b", "3", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "c", "3", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "c", "4", "61", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "c", "5", "68", "LKSHIP")
        );
        Assert.assertTrue(isWaiting(trolleyOrders, "a"));
    }

    @Test
    public void testIsWaiting_C_NotInLKSHIP() {
        List<ShipmentDao.TrolleyOrder> trolleyOrders = Arrays.asList(
                new ShipmentDao.TrolleyOrder(
                        "a", "1", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "a", "2", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "b", "1", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "b", "3", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "c", "3", "55", "LKSHIP"),
                new ShipmentDao.TrolleyOrder(
                        "c", "4", "55", "LKCONS")
        );
        Assert.assertFalse(isWaiting(trolleyOrders, "a"));
    }

}

