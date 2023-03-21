package com.library.dao;

import com.library.bean.ClassInfo;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ClazzDao {
    private final static String NAMESPACE = "com.library.dao.ClazzDao.";
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;
    public List<ClassInfo> getAllClassInfo(){
        return sqlSessionTemplate.selectList(NAMESPACE + "getAll");
    }
}
