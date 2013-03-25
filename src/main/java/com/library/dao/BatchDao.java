package com.library.dao;

import com.library.bean.Batch;
import com.library.bean.BatchResult;
import com.library.bean.Book;
import com.library.bean.Result;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("batchDao")
public class BatchDao {
    private final static String NAMESPACE = "com.library.dao.BatchDao.";
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    public List<Result> findResult(){
        return sqlSessionTemplate.selectList(NAMESPACE+"findResult");
    }
    public List<Result> findSuccessResult(){
        return sqlSessionTemplate.selectList(NAMESPACE+"findSuccessResult");
    }
    public int batchInsert(final Batch batch){
        return sqlSessionTemplate.insert(NAMESPACE + "insertBatch", batch);
    }
    public List<Result> findFailResult(){
        return sqlSessionTemplate.selectList(NAMESPACE+"findFailResult");
    }
    public List<Result> findHandlingResult(){
        return sqlSessionTemplate.selectList(NAMESPACE+"findHandlingResult");
    }

    public Batch queryByIsbn(final String isbn){
        return sqlSessionTemplate.selectOne(NAMESPACE + "queryByIsbn", isbn);
    }

    public int updateByIsbn(final Batch batch){
        return sqlSessionTemplate.update(NAMESPACE+"updateBatch",batch);
    }

    public void changeStruc(){
        sqlSessionTemplate.update(NAMESPACE+"changeStruc");
    }
}
