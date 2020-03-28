package com.hongyu.service;

import com.grain.service.BaseService;
import com.hongyu.entity.ProfitShareConfirm;

public interface ProfitShareConfirmService extends BaseService<ProfitShareConfirm, Long> {
	public void calculateProfitshareConfirmPerMonth() throws Exception;
	public void calculateProfitshareConfirmCurMonth() throws Exception;
}