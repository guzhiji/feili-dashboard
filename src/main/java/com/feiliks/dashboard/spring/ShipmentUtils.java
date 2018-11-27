package com.feiliks.dashboard.spring;

import java.util.*;

class ShipmentUtils {

    static void check(
            List<ShipmentDao.TrolleyOrder> trolleyOrders,
            List<ShipmentDao.Trolley> trolleyList,
            Map<String, Integer> orderStats) {

        Map<String, Set<ShipmentDao.TrolleyOrder>> groupByTrolley = new HashMap<>();
        Map<String, Set<ShipmentDao.TrolleyOrder>> groupByOrder = new HashMap<>();
        groupTrolleyOrderPairs(trolleyOrders, groupByTrolley, groupByOrder);

        Set<String> aptOrders = new HashSet<>();
        Set<String> waitOrders = new HashSet<>();
        Set<String> unfnOrders = new HashSet<>();
        Set<String> lkShipTrolleys = new HashSet<>();
        for (ShipmentDao.Trolley t : trolleyList) {
            String tid = t.getTrolleyId();
            lkShipTrolleys.add(tid);
            Set<ShipmentDao.TrolleyOrder> orders = groupByTrolley.get(tid);
            if (t.getAppointmentKey() != null &&
                !t.getAppointmentKey().isEmpty()) {
                t.status = ShipmentDao.Status.APPOINTMENT.name();
                if (orders != null) {
                    for (ShipmentDao.TrolleyOrder o : orders)
                        aptOrders.add(o.getOrderKey());
                }
            } else if (isWaiting(groupByTrolley, groupByOrder,
                new HashSet<String>(), tid)) {
                t.status = ShipmentDao.Status.WAITING.name();
                if (orders != null) {
                    for (ShipmentDao.TrolleyOrder o : orders)
                        waitOrders.add(o.getOrderKey());
                }
            } else {
                t.status = ShipmentDao.Status.UNFINISHED.name();
                if (orders != null) {
                    for (ShipmentDao.TrolleyOrder o : orders)
                        unfnOrders.add(o.getOrderKey());
                }
            }
        }

        for (ShipmentDao.TrolleyOrder to : trolleyOrders) {
            String tid = to.getTrolleyId();
            if (lkShipTrolleys.contains(tid)) continue;
            // trolleys outside LKSHIP
            Set<ShipmentDao.TrolleyOrder> orders = groupByTrolley.get(tid);
            if (orders == null) continue;
            for (ShipmentDao.TrolleyOrder to2 : orders)
                unfnOrders.add(to2.getOrderKey());
        }
        orderStats.put(ShipmentDao.Status.APPOINTMENT.name(), aptOrders.size());
        orderStats.put(ShipmentDao.Status.WAITING.name(), waitOrders.size());
        orderStats.put(ShipmentDao.Status.UNFINISHED.name(), unfnOrders.size());

    }

    static void groupTrolleyOrderPairs(
            List<ShipmentDao.TrolleyOrder> trolleyOrders,
            Map<String, Set<ShipmentDao.TrolleyOrder>> groupByTrolley,
            Map<String, Set<ShipmentDao.TrolleyOrder>> groupByOrder) {
        for (ShipmentDao.TrolleyOrder to : trolleyOrders) {

            String tid = to.getTrolleyId();
            Set<ShipmentDao.TrolleyOrder> orders = groupByTrolley.get(tid);
            if (orders == null) {
                orders = new HashSet<>();
                orders.add(to);
                groupByTrolley.put(tid, orders);
            } else {
                orders.add(to);
            }

            String okey = to.getOrderKey();
            Set<ShipmentDao.TrolleyOrder> trolleys = groupByOrder.get(okey);
            if (trolleys == null) {
                trolleys = new HashSet<>();
                trolleys.add(to);
                groupByOrder.put(okey, trolleys);
            } else {
                trolleys.add(to);
            }

        }
    }

    static boolean isWaiting(
            Map<String, Set<ShipmentDao.TrolleyOrder>> groupByTrolley,
            Map<String, Set<ShipmentDao.TrolleyOrder>> groupByOrder,
            Set<String> checkedTrolleys,
            String trolleyId) {
        checkedTrolleys.add(trolleyId);
        // get orders in this trolley
        Set<ShipmentDao.TrolleyOrder> orders = groupByTrolley.get(trolleyId);
        if (orders == null) return false;
        // check whether all orders are picked in this trolley
        for (ShipmentDao.TrolleyOrder to : orders) {
            if (!"LKSHIP".equals(to.getPutawayZone()))
                return false;
            if (!"55".equals(to.getOrderStatus()))
                return false;
            // get trolleys that contain the current order
            Set<ShipmentDao.TrolleyOrder> trolleys = groupByOrder.get(to.getOrderKey());
            if (trolleys == null) return false;
            // check whether all orders in these trolleys are picked
            for (ShipmentDao.TrolleyOrder to2 : trolleys) {
                String trolleyId2 = to2.getTrolleyId();
                if (!checkedTrolleys.contains(trolleyId2) &&
                    !isWaiting(groupByTrolley, groupByOrder,
                        checkedTrolleys, trolleyId2))
                    return false;
            }
        }
        return true;
    }

}
