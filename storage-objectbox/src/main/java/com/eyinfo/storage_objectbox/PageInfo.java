package com.eyinfo.storage_objectbox;

import java.io.Serializable;
import java.util.List;

public class PageInfo<T> implements Serializable {
    /**
     * 当前页码
     */
    private int page;

    /**
     * 上次查询最后一条数据时间戳
     */
    private Long lastTimestamp;

    /**
     * 每页数量
     */
    private int limit;

    /**
     * 本次查询总记录数
     */
    private int total;

    /**
     * 本次查询结果
     */
    private List<T> list;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(Long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
