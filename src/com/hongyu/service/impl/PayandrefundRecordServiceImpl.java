package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.PayandrefundRecord;
import com.hongyu.service.PayandrefundRecordService;

@Service("payandrefundRecordServiceImpl")
public class PayandrefundRecordServiceImpl extends BaseServiceImpl<PayandrefundRecord,Long> implements PayandrefundRecordService {

	@Resource(name="payandrefundRecordDaoImpl")
	@Override
	public void setBaseDao(BaseDao<PayandrefundRecord, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
