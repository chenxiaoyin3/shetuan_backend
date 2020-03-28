package com.hongyu.service;


import java.util.List;

import com.grain.service.BaseService;
import com.hongyu.Pageable;
import com.hongyu.controller.WeBusinessDivideController.WrapBusinessDividePage;
import com.hongyu.controller.WeBusinessDivideController.WrapStoreDividePage;
import com.hongyu.entity.WeDivideReport;

public interface WeDivideReportService extends BaseService<WeDivideReport, Long> {
	public WrapStoreDividePage findStoreDividePage(Pageable pageable, String startDate, String endDate, Integer type, String storeName, Boolean isPageable);
	public WrapBusinessDividePage findBusinessDividePage(Pageable pageable, String startDate, String endDate, Integer type, Long storeId, String businessName, Boolean isPageable);
	public WrapBusinessDividePage findBusinessDetailDividePage(Pageable pageable, String startDate, String endDate, Integer type, Long storeId, Long businessId, Boolean isPageable);
	/**
	 * 查找满足转账条件的微商
	 * @return
	 */
	public List<Long> findWeBusinessSatisfyTransferCondition();
}
