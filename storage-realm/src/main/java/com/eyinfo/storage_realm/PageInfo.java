package com.eyinfo.storage_realm;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
