package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.MhLineRefund;
import com.hongyu.service.MhLineRefundService;
@Service("mhLineRefundServiceImpl")
public class MhLineRefundServiceImpl extends BaseServiceImpl<MhLineRefund, Long> implements MhLineRefundService{

	@Override
	@Resource(name = "mhLineRefundDaoImpl")
	public void setBaseDao(BaseDao<MhLineRefund, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
	

}
