package com.hongyu.task.impl;

import java.util.Date;

import javax.annotation.Resource;

import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.ProfitShareConfirmDetailService;
import com.hongyu.task.Processor;
import com.hongyu.util.DateUtil;

public class ProfitShareConfirmDetailProcessor implements Processor {
	
	@Resource(name = "profitShareConfirmDetailServiceImpl")
	ProfitShareConfirmDetailService profitShareConfirmDetailServiceImpl;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderServiceImpl;

	@Override
	public void process() {
		//获取前一天时间
		Date yesterday = DateUtil.getPreDay(new Date());
		Date startOfYesterday = DateUtil.getStartOfDay(yesterday);
		Date endOfYesterday = DateUtil.getEndOfDay(yesterday);
		
		
	}

}
