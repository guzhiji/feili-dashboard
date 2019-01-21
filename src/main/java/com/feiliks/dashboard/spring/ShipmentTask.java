package com.feiliks.dashboard.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ShipmentTask {

    @Autowired
    private SimpMessagingTemplate wsShipment;

    @Autowired
    private ShipmentDao dao;

    private List<ShipmentDao.Trolley> table = null;
    private List<ShipmentDao.Appointment> appointments = new ArrayList<>();
    private final Map<String, Integer> ordersByStatus = new HashMap<>();

    public ShipmentTask() {
        for (ShipmentDao.Status status : ShipmentDao.Status.values())
            ordersByStatus.put(status.name(), 0);
    }

    private void broadcast(String msg) {
        wsShipment.convertAndSend("/dashboard/shipment", msg);
    }

    private static String stringifyStats(Map<String, Integer> stats) {
        StringBuilder out = new StringBuilder();
        for (Map.Entry<String, Integer> item : stats.entrySet())
            out.append(item.getKey())
                .append('=')
                .append(item.getValue())
                .append(';');
        return out.toString();
    }

    public List<ShipmentDao.Trolley> getTable() {
        return table;
    }

    public Map<String, Integer> getStatus() {
        return ordersByStatus;
    }

    public List<ShipmentDao.Appointment> getAppointments() {
        return appointments;
    }

    @Scheduled(fixedDelay = 5000)
    public void run() {

        List<ShipmentDao.Trolley> trolleys = dao.getTrolleys();
        Map<String, Integer> counts = new HashMap<>();
        boolean updated = false;
        // 1. set trolley status
        // 2. count orders by trolley status
        ShipmentUtils.check(dao.getTrolleyOrders(), trolleys, counts);
        for (ShipmentDao.Status status : ShipmentDao.Status.values()) {
            String k = status.name();
            Integer n = counts.get(k);
            if (n != null && !n.equals(ordersByStatus.get(k))) {
                updated = true;
                ordersByStatus.put(k, n);
            }
        }
        if (updated) broadcast("pie:" + stringifyStats(ordersByStatus));
        table = trolleys;

        List<ShipmentDao.Appointment> newAppointments = dao.getAppointments();
        Set<ShipmentDao.Appointment> removed = new HashSet<>(appointments);
        Set<ShipmentDao.Appointment> added = new HashSet<>(newAppointments);
        removed.removeAll(newAppointments); // removed: not in new appointments
        added.removeAll(appointments); // added: not in old appointments
        // update appointments
        appointments = newAppointments;
        // notify updates
        for (ShipmentDao.Appointment a : removed) {
            broadcast("bar:remove:" + a.toString());
            updated = true;
        }
        for (ShipmentDao.Appointment a : added) {
            broadcast("bar:add:" + a.toString());
            updated = true;
        }

        if (!updated) broadcast("heartbeat:" + System.currentTimeMillis());

    }

}

