package com.hongyu.task.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hongyu.service.ProfitShareConfirmService;
import com.hongyu.task.Processor;

@Component("profitShareConfirmProcessor")
public class ProfitShareConfirmProcessor implements Processor {
	
	@Resource(name="profitShareConfirmServiceImpl")
	ProfitShareConfirmService profitShareConfirmService;

	@Override
	public void process() {
		try {
			profitShareConfirmService.calculateProfitshareConfirmPerMonth();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
