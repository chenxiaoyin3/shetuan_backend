package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.Insurance;
import com.hongyu.service.InsuranceService;

@Service("insuranceServiceImpl")
public class InsuranceServiceImpl extends BaseServiceImpl<Insurance,Long> implements InsuranceService{

	
	@Override
	@Resource(name="insuranceDaoImpl")
	public void setBaseDao(BaseDao<Insurance, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

	@Override
	public Insurance getExtraInsuranceOfOrder(HyOrder order) {
		// TODO Auto-generated method stub
		if(order.getOrderItems()==null){
			return null;
		}
		for(HyOrderItem item:order.getOrderItems()){
			if(item.getHyOrderCustomers()==null){
				continue;
			}
			for(HyOrderCustomer customer:item.getHyOrderCustomers()){
				if(customer.getIsInsurance()!=null && customer.getIsInsurance()){
					Insurance insurance = find(customer.getInsuranceId());
					return insurance;
				}
				
			}
			
		}
		return null;
	}

	
	
	
}
