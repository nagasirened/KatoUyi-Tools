package com.katouyi.tools.elasticSearch2;

import java.util.List;

public class Pager<T> {
    public static final Integer MAX_PAGE_SIZE = 500;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalCount;
    private Integer pageCount;
    private List<T> list;

    public Pager() {
        this.pageNumber = 1;
        this.pageSize = 20;
        this.totalCount = 0L;
        this.pageCount = 0;
    }

    public Pager(String pageNumber, String pageSize) {
        this(Integer.valueOf(pageNumber), Integer.valueOf(pageSize));
    }

    public Pager(int pageNumber, int pageSize) {
        this.pageNumber = 1;
        this.pageSize = 20;
        this.totalCount = 0L;
        this.pageCount = 0;
        this.pageNumber = pageNumber;
        this.setPageSize(pageSize);
    }

    public Integer getPageNumber() {
        return this.pageNumber < 1 ? 1 : this.pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        if (pageSize < 1) {
            pageSize = 1;
        } else if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }

        this.pageSize = pageSize;
    }

    public Long getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getPageCount() {
        this.pageCount = Math.toIntExact(this.totalCount / (long)this.pageSize);
        if (this.totalCount % (long)this.pageSize > 0L) {
            Integer var1 = this.pageCount;
            Integer var2 = this.pageCount = this.pageCount + 1;
        }

        return this.pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public List<T> getList() {
        return this.list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
