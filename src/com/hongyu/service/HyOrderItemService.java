package com.hongyu.service;

import java.math.BigDecimal;

import com.grain.service.BaseService;
import com.hongyu.entity.HyOrderItem;

public interface HyOrderItemService extends BaseService<HyOrderItem, Long> {
	public BigDecimal getBaoxianJiesuanPrice(HyOrderItem item);
	public BigDecimal getBaoxianWaimaiPrice(HyOrderItem item);

}
