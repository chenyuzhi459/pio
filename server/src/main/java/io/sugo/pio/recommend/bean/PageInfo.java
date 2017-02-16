package io.sugo.pio.recommend.bean;

import java.io.Serializable;

public class PageInfo implements Serializable {
    private static final int DEFAULT_SIZE = 10;
    private int pageIndex = 1;
    private int pageSize = DEFAULT_SIZE;

    public int getPageIndex() {
        if (pageIndex < 1) {
            return 1;
        }
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        if(pageSize < 1){
            return DEFAULT_SIZE;
        }
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageOffset() {
        return (getPageIndex() - 1) * getPageSize();
    }
}
