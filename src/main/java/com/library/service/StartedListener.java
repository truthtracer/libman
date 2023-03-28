package com.library.service;

import com.library.dao.BatchDao;
import com.library.dao.BookDao;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class StartedListener implements ServletContextListener{
//    final ScheduledExecutorService sc= Executors.newScheduledThreadPool(10);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(servletContextEvent.getServletContext());
        BookDao bookDao=ac.getBean(BookDao.class);
        bookDao.changeStruc();
        try{
            BatchDao batchDao = ac.getBean(BatchDao.class);
            batchDao.changeStruc();

        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            BatchDao batchDao = ac.getBean(BatchDao.class);

            batchDao.changePrimary();

        }catch (Exception e){
            e.printStackTrace();
        }
//        sc.scheduleWithFixedDelay(() -> {
//
//        },1000, 1000, TimeUnit.SECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

}
