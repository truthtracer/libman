package com.library.bean;

public class BatchResult {
    private int total;
    private int success;
    private int fail;
    private int notfound;
    private String batchNo;
    private int handling;

    public int getHandling() {
        return handling;
    }

    public void setHandling(int handling) {
        this.handling = handling;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFail() {
        return fail;
    }

    public void setFail(int fail) {
        this.fail = fail;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public int getNotfound() {
        return notfound;
    }

    public void setNotfound(int notfound) {
        this.notfound = notfound;
    }
}
