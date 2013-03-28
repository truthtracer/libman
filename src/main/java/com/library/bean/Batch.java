package com.library.bean;

public class Batch {
    private String batchNo;
    /**
     * 0-失败 1-成功  2-未找到书籍的
     */
    private Integer status;
    private String isbn;
    private String failCause;

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getFailCause() {
        return failCause;
    }

    public void setFailCause(String failCause) {
        if(failCause!= null){
            if(failCause.length() > 200){
                failCause = failCause.substring(0, 200);
            }
        }
        this.failCause = failCause;
    }
}
