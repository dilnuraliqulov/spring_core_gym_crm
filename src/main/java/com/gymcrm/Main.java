package com.gymcrm;

import com.gymcrm.config.WebConfig;
import com.gymcrm.filter.TransactionLoggingFilter;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws LifecycleException {
        log.info("Starting Gym CRM Application...");

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector(); // initialize connector

        Context context = tomcat.addContext("", null);

        // Register Transaction Logging Filter
        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName("transactionLoggingFilter");
        filterDef.setFilterClass(TransactionLoggingFilter.class.getName());
        context.addFilterDef(filterDef);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("transactionLoggingFilter");
        filterMap.addURLPattern("/*");
        context.addFilterMap(filterMap);

        // Spring Web Application Context
        AnnotationConfigWebApplicationContext springContext =
                new AnnotationConfigWebApplicationContext();
        springContext.register(WebConfig.class);

        DispatcherServlet dispatcherServlet =
                new DispatcherServlet(springContext);

        Tomcat.addServlet(context, "dispatcher", dispatcherServlet);
        context.addServletMappingDecoded("/", "dispatcher");

        log.info("Gym CRM Application starting on port 8080");
        log.info("Swagger UI available at: http://localhost:8080/swagger-ui/index.html");

        tomcat.start();
        tomcat.getServer().await();
    }
}
