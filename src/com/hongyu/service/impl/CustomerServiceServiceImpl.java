package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.CustomerService;
import com.hongyu.service.CustomerServiceService;

@Service("customerServiceServiceImpl")
public class CustomerServiceServiceImpl extends BaseServiceImpl<CustomerService, Long> implements CustomerServiceService {

	@Override
	@Resource(name="customerServiceDaoImpl")
	public void setBaseDao(BaseDao<CustomerService, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
