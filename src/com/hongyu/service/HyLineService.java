package com.hongyu.service;

import java.math.BigDecimal;
import java.util.Map;

import com.grain.service.BaseService;
import com.hongyu.entity.HyLine;

public interface HyLineService extends BaseService<HyLine, Long> {
    public BigDecimal getLineLowestPrice(HyLine hyLine);

    public Object[] getMemoDetail(HyLine hyLine);
}
