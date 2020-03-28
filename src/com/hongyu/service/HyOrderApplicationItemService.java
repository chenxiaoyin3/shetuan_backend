package com.hongyu.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.grain.service.BaseService;
import com.hongyu.entity.HyOrderApplicationItem;
import com.hongyu.entity.HyOrderItem;

public interface HyOrderApplicationItemService extends BaseService<HyOrderApplicationItem, Long> {
	
	public Map<String, Object> auditItemHelper(HyOrderApplicationItem item) throws Exception;

	public List<HyOrderApplicationItem> getItemsByOrderItem(Long itemId);

	public HyOrderApplicationItem getTotalRefund(List<HyOrderApplicationItem> items);
}

