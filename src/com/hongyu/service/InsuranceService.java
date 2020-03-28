package com.hongyu.service;

import com.grain.service.BaseService;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.Insurance;

public interface InsuranceService extends BaseService<Insurance, Long> {
	
	Insurance getExtraInsuranceOfOrder(HyOrder order);

}
