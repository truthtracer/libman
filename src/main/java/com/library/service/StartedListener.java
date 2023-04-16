package com.library.service;

import com.library.dao.BatchDao;
import com.library.dao.BookDao;
import com.library.dao.LendDao;
import com.library.dao.ReaderInfoDao;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

@Component
public class StartedListener implements ServletContextListener{

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(servletContextEvent.getServletContext());
        BookDao bookDao = ac.getBean(BookDao.class);
        try {
            bookDao.changeStruc();
        }catch (Exception e){
            e.printStackTrace();
        }
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
        try{
            ReaderInfoDao readerInfoDao = ac.getBean(ReaderInfoDao.class);
            readerInfoDao.changeStruc();
            readerInfoDao.initReaderNo();
        }catch (Exception exc){
            exc.printStackTrace();
        }
        try{
            ReaderInfoDao readerInfoDao =ac.getBean(ReaderInfoDao.class);
            readerInfoDao.dropNotNull();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        try{
            LendDao lendDao = ac.getBean(LendDao.class);
            lendDao.changeStruc();
        }catch (Exception exc){
            exc.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
