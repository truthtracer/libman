package com.library.vo;

import com.library.bean.Lend;

public class LendVo extends Lend {
    private String readerName;
    private Long readerCardNo;
    private String bookName;
    private Long readerNo;

    public String getBookName() {
        return bookName;
    }

    public Long getReaderNo() {
        return readerNo;
    }

    public void setReaderNo(Long readerNo) {
        this.readerNo = readerNo;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

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
