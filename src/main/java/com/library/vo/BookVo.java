package com.library.vo;

import com.library.bean.Book;

public class BookVo extends Book {
    /**
     * 是否有借出但是尚未归还的记录
     */
    private boolean hasLendRecord;

    public boolean isHasLendRecord() {
        return hasLendRecord;
    }

    public void setHasLendRecord(boolean hasLendRecord) {
        this.hasLendRecord = hasLendRecord;
    }
}
