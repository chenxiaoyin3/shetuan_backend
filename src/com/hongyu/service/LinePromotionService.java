package com.hongyu.service;


import com.grain.service.BaseService;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.LinePromotion;

public interface LinePromotionService extends BaseService<LinePromotion, Long> {
	public LinePromotion findByGroupId(Long id);
//	public List<LinePromotion> findByGroupId(Long id);
	public Page<LinePromotion> findAuditPage(HyAdmin auditor, Pageable pageable, LinePromotion query);

}
