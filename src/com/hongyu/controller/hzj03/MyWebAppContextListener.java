//package com.hongyu.controller.hzj03;
//
//import javax.servlet.ServletContextEvent;
//import javax.servlet.ServletContextListener;
//import java.sql.Driver;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.util.Enumeration;
//
///**
// * Created by xyy on 2019/5/10.
// *
// * @author xyy
// *
// * 实现ServletContextListener的监听器, 需要注册到web.xml中
// */
//public class MyWebAppContextListener implements ServletContextListener {
//    @Override
//    public void contextInitialized(ServletContextEvent servletContextEvent) {
//    }
//
//    @Override
//    public void contextDestroyed(ServletContextEvent servletContextEvent) {
//        // 在tomcat停止时将JDBC驱动取消注册 解决"To prevent a memory leak, the JDBC Driver has been forcibly unregistered"的警告
//        // Get the webapp's ClassLoader
//        ClassLoader cl = Thread.currentThread().getContextClassLoader();
//        Enumeration<Driver> drivers = DriverManager.getDrivers();
//        while (drivers.hasMoreElements()) {
//            Driver driver = drivers.nextElement();
//            if (driver.getClass().getClassLoader() == cl) {
//                // This driver was registered by the webapp's ClassLoader, so deregister it:
//                try {
//                    DriverManager.deregisterDriver(driver);
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                }
//            } else {
//                // driver was not registered by the webapp's ClassLoader and may be in use elsewhere
//                System.out.println("Not deregistering JDBC driver {} as it does not belong to this webapp's ClassLoader" + driver);
//            }
//        }
//    }
//}
