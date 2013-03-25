package com.library.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.library.bean.Batch;
import com.library.bean.BatchResult;
import com.library.bean.Result;
import com.library.dao.BatchDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class BatchService {
    private Logger log = Logger.getLogger(this.getClass());
    SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
    CompletionService<Integer> completionService= new ExecutorCompletionService<>(Executors.newFixedThreadPool(10));
    @Autowired
    private BatchDao batchDao;

//    public int insertBatch(Batch batch){
//       return batchDao.batchInsert(batch);
//    }
    public List<BatchResult> findResult(){
        List<Result> allResult = batchDao.findResult();
        List<Result> handlingResult= batchDao.findHandlingResult();
        List<Result> successResult= batchDao.findSuccessResult();
        List<Result> failedResult = batchDao.findFailResult();
        List<BatchResult> allRs=new ArrayList<>();
        for(Result rs : allResult){
            BatchResult batchResult = new BatchResult();
            batchResult.setBatchNo(rs.getBatchNo());
            batchResult.setTotal(rs.getCt());
            Optional<Result> op=handlingResult.stream().filter(r->r.getBatchNo().equals(rs.getBatchNo())).findFirst();
            if(op.isPresent())
                batchResult.setHandling(op.get().getCt());
            else
                batchResult.setHandling(0);

            Optional<Result> opSuccess=successResult.stream().filter(r->r.getBatchNo().equals(rs.getBatchNo())).findFirst();
            opSuccess.ifPresent(result -> batchResult.setSuccess(result.getCt()));

            Optional<Result> opFail = failedResult.stream().filter(r->r.getBatchNo().equals(rs.getBatchNo())).findFirst();
            opFail.ifPresent(result-> batchResult.setFail(result.getCt()));
            allRs.add(batchResult);
        }
        return allRs;
    }

    public List<String> insertBatchLst(final List<String> isbns){
        final List<String> lstResult=new ArrayList<>();
        final AtomicInteger atomicInteger=new AtomicInteger(0);
        List<Future<Integer>> lst = new ArrayList<>();
        List<Batch> batches = new ArrayList<>();
        final String batchNo = sdf.format(new Date());
        for(String ibn : isbns){
            if(StringUtils.isEmpty(ibn)||
                    (!StringUtils.isEmpty(ibn) && ibn.contains("Content-"))
            || (!StringUtils.isEmpty(ibn) && ibn.contains("WebKit"))){
                log.warn(batchNo+",空行or 非法行 skipped");
                continue;
            }
            Batch b = new Batch();
            b.setIsbn(ibn.trim());
            b.setBatchNo(batchNo);
            batches.add(b);
        }
         for(Batch b : batches){
             lst.add(completionService.submit(()->{
                 try {
                     Batch batch = batchDao.queryByIsbn(b.getIsbn());
                     if (batch == null) {
                         int x = batchDao.batchInsert(b);
                         if (x > 0) {
                             lstResult.add(b.getIsbn());
                             atomicInteger.addAndGet(1);
                         }
                         return 1;
                     } else {
                         lstResult.add(b.getIsbn());
                         return 0;
                     }
                 }catch (Exception e){
                     log.error(e.getMessage(),e);
                     return 0;
                 }
             }));
         }
         lst.forEach(r->{
             try{
                 completionService.take().get(2, TimeUnit.SECONDS);
             }catch (Exception e){
                 log.error(e.getMessage(),e);
             }
         });
         log.info(batchNo+", total isbn: "+ atomicInteger.get());
         return lstResult;
    }

    public void changeStruc(){
        batchDao.changeStruc();
    }
}
