package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.HyPaymentpreJiangtai;
import com.hongyu.service.HyPaymentpreJiangtaiService;

@Service("hyPaymentpreJiangtaiServiceImpl")
public class HyPaymentpreJiangtaiServiceImpl extends BaseServiceImpl<HyPaymentpreJiangtai, Long> implements HyPaymentpreJiangtaiService{

	@Override
	@Resource(name="hyPaymentpreJiangtaiDaoImpl")
	public void setBaseDao(BaseDao<HyPaymentpreJiangtai, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
	

}
