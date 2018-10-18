package com.feiliks.dashboard.javax;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DashboardContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DashboardServer.shutdown();
    }

}
