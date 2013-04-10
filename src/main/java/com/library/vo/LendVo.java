package com.library.vo;

import com.library.bean.Lend;

public class LendVo extends Lend {
    private String readerName;
    private Long readerCardNo;

    public String getReaderName() {
        return readerName;
    }

    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    public Long getReaderCardNo() {
        return readerCardNo;
    }

    public void setReaderCardNo(Long readerCardNo) {
        this.readerCardNo = readerCardNo;
    }
}
