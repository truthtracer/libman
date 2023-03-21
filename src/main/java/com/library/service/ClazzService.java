package com.library.service;

import com.library.bean.ClassInfo;
import com.library.dao.ClazzDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class ClazzService {
    @Autowired
    private ClazzDao clazzDao;

    public List<ClassInfo> getAll(){
        return clazzDao.getAllClassInfo();
    }
}
