package com.hongyu.service;

import java.util.List;
import java.util.Map;

import com.grain.service.BaseService;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.Inbound;

public interface InboundService extends BaseService<Inbound, Long> {

	Integer substractInbound(Integer total,List<Inbound> inbounds, BusinessOrderItem orderItem, HyAdmin username);
	Integer getInboundTotal(List<Inbound> inbounds);
	BusinessOrderItem isInboundEnough(List<BusinessOrderItem> items);
	void updateOrderItemInbound(BusinessOrderItem item, HyAdmin username);
	Integer findInboundUniqueSpecificationTotal(Long categoryId, String specialtyName);
	public List<Map<String, Object>> getMergedInboundByPage(int page, int pageSize, Long categoryId, String specialtyName);
	public List<Inbound> getInboundListBySpecificationId(Long specificationId,Integer quantity);
}
