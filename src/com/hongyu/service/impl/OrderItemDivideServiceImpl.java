package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.OrderItemDivide;
import com.hongyu.service.OrderItemDivideService;
@Service("orderItemDivideServiceImpl")
public class OrderItemDivideServiceImpl extends BaseServiceImpl<OrderItemDivide, Long> implements OrderItemDivideService {

	@Override
	@Resource(name="orderItemDivideDaoImpl")
	public void setBaseDao(BaseDao<OrderItemDivide, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
