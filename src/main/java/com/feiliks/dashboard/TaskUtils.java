package com.feiliks.dashboard;

public class TaskUtils {

    private static boolean validateClass(String javaClass, Class<?> iface) {
        if (javaClass == null || javaClass.isEmpty())
            return false;
        try {
            Class<?> cls = Class.forName(javaClass);
            return iface.isAssignableFrom(cls);
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    public static boolean validateMonitor(String javaClass) {
        return validateClass(javaClass, AbstractMonitor.class);
    }

}
