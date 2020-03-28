package com.hongyu.task.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hongyu.service.AddedServiceService;
import com.hongyu.task.Processor;

/**增值业务打款扫描 */
@Component("valueAddedProcessor")
public class ValueAddedProcessor implements Processor {
	@Resource(name = "addedServiceServiceImpl")
	AddedServiceService AddedServiceService;
	
	@Override
	public void process() {
		try {
			AddedServiceService.insertApplySubmitAuto();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
