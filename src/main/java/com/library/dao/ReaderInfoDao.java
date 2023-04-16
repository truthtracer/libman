package com.library.dao;

import com.library.bean.ReaderInfo;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ReaderInfoDao {

    private final static String NAMESPACE = "com.library.dao.ReaderInfoDao.";
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    public Long queryLastReaderInfo(){
        return sqlSessionTemplate.selectOne(NAMESPACE +"queryLastReaderInfo");
    }
    public ArrayList<ReaderInfo> getAllReaderInfo() {
        List<ReaderInfo> result = sqlSessionTemplate.selectList(NAMESPACE + "getAllReaderInfo");
        return (ArrayList<ReaderInfo>) result;
    }

    public ReaderInfo findReaderInfoByReaderId(final long reader_id) {
        return sqlSessionTemplate.selectOne(NAMESPACE + "findReaderInfoByReaderId", reader_id);
    }

    public int deleteReaderInfo(final long reader_id) {
        return sqlSessionTemplate.delete(NAMESPACE + "deleteReaderInfo", reader_id);
    }
    public void initReaderNo(){
        sqlSessionTemplate.update(NAMESPACE +"initReaderNo");
    }

    public int editReaderInfo(final ReaderInfo readerInfo) {
        return sqlSessionTemplate.update(NAMESPACE + "editReaderInfo", readerInfo);
    }

    public int editReaderCard(final ReaderInfo readerInfo) {
        return sqlSessionTemplate.update(NAMESPACE + "editReaderCard", readerInfo);
    }

    public void changeStruc(){
         sqlSessionTemplate.update(NAMESPACE +"changeStruc");
    }
    public void dropNotNull(){
        sqlSessionTemplate.update(NAMESPACE+"dropNotNull");
    }
    public final long addReaderInfo(final ReaderInfo readerInfo) {
        if (sqlSessionTemplate.insert(NAMESPACE + "addReaderInfo", readerInfo) > 0) {
            return sqlSessionTemplate.selectOne(NAMESPACE + "getReaderId", readerInfo);
        } else {
            return -1;
        }
    }
    public ReaderInfo getRed(Long readerNo){
        return sqlSessionTemplate.selectOne(NAMESPACE+"getRed",readerNo);
    }
}
