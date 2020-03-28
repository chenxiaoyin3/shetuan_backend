package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.service.HyOrderItemService;

@Service("hyOrderItemServiceImpl")
public class HyOrderItemServiceImpl extends BaseServiceImpl<HyOrderItem,Long> implements HyOrderItemService {

	@Resource(name="hyOrderItemDaoImpl")
	@Override
	public void setBaseDao(BaseDao<HyOrderItem, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

	public BigDecimal getBaoxianJiesuanPrice(HyOrderItem item){
		List<HyOrderCustomer> customers = item.getHyOrderCustomers();
		if(customers==null)
			return BigDecimal.ZERO;
		BigDecimal price = BigDecimal.ZERO;
		for(HyOrderCustomer customer:customers){
			if(customer.getIsInsurance()!=null && customer.getIsInsurance()){
				price=price.add(customer.getSettlementPrice());
			}
		}
		return price;
	}
	public BigDecimal getBaoxianWaimaiPrice(HyOrderItem item){
		List<HyOrderCustomer> customers = item.getHyOrderCustomers();
		if(customers==null)
			return BigDecimal.ZERO;
		BigDecimal price = BigDecimal.ZERO;
		for(HyOrderCustomer customer:customers){
			if(customer.getIsInsurance() != null && customer.getIsInsurance()){
				price=price.add(customer.getSalePrice());
			}
		}
		return price;
		
	}
}
