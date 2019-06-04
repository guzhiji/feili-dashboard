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
        Map<String, ShipmentDao.Status> trolleyStatus = new HashMap<>();
        for (ShipmentDao.Trolley t : trolleyList) {
            String tid = t.getTrolleyId();
            ShipmentDao.Status s = trolleyStatus.get(tid);
            if (t.getAppointmentKey() != null &&
                !t.getAppointmentKey().isEmpty()) {
                // appointment status
                if (s == null) {
                    trolleyStatus.put(tid, ShipmentDao.Status.APPOINTMENT);
                }
            } else if (isWaiting(groupByTrolley, groupByOrder,
                    new HashSet<String>(), tid)) {
                // waiting status
                if (s == null || s.ordinal() > ShipmentDao.Status.WAITING.ordinal()) {
                    trolleyStatus.put(tid, ShipmentDao.Status.WAITING);
                }
            } else {
                // unfinished status
                if (s == null || s.ordinal() > ShipmentDao.Status.UNFINISHED.ordinal()) {
                    trolleyStatus.put(tid, ShipmentDao.Status.UNFINISHED);
                }
            }
        }
        for (ShipmentDao.Trolley t : trolleyList) {
            String tid = t.getTrolleyId();
            Set<ShipmentDao.TrolleyOrder> orders = groupByTrolley.get(tid);
            ShipmentDao.Status s = trolleyStatus.get(tid);
            if (s != null) {
                // set trolley status
                t.status = s.name();
                if (orders != null) {
                    // count orders by status
                    switch (s) {
                        case APPOINTMENT:
                            for (ShipmentDao.TrolleyOrder o : orders) {
                                String okey = o.getOrderKey();
                                if (!waitOrders.contains(okey) &&
                                        !unfnOrders.contains(okey))
                                    aptOrders.add(okey);
                            }
                            break;
                        case WAITING:
                            for (ShipmentDao.TrolleyOrder o : orders) {
                                String okey = o.getOrderKey();
                                if (unfnOrders.contains(okey))
                                    continue;
                                aptOrders.remove(okey);
                                waitOrders.add(okey);
                            }
                            break;
                        case UNFINISHED:
                            for (ShipmentDao.TrolleyOrder o : orders) {
                                String okey = o.getOrderKey();
                                aptOrders.remove(okey);
                                waitOrders.remove(okey);
                                unfnOrders.add(okey);
                            }
                            break;
                    }
                }
            }
        }

        // find extra orders on trolleys outside LKSHIP
        for (ShipmentDao.TrolleyOrder to : trolleyOrders) {
            String tid = to.getTrolleyId();
            // orders with appointment keys are not 'UNFINISHED'
            if (to.getAppointmentKey() != null) continue;
            // skip trolleys inside LKSHIP
            if (trolleyStatus.containsKey(tid)) continue;
            // trolleys outside LKSHIP
            Set<ShipmentDao.TrolleyOrder> orders = groupByTrolley.get(tid);
            if (orders == null) continue;
            for (ShipmentDao.TrolleyOrder to2 : orders) {
                String okey = to2.getOrderKey();
                aptOrders.remove(okey);
                waitOrders.remove(okey);
                unfnOrders.add(okey);
            }
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
            // if (!"55".equals(to.getOrderStatus()))
            if (to.getOrderStatus() == null)
                return false;
            try {
                // < 55 -> not yet picked -> not WAITING status
                if (55 > Integer.parseInt(to.getOrderStatus().trim()))
                    return false;
            } catch (NumberFormatException ignored) {
                return false;
            }
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
