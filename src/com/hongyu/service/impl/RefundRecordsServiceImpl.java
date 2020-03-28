package com.hongyu.service.impl;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.RefundRecordsDao;
import com.hongyu.entity.RefundRecords;
import com.hongyu.service.RefundRecordsService;

@Service("refundRecordsServiceImpl")
public class RefundRecordsServiceImpl extends BaseServiceImpl<RefundRecords, Long> implements RefundRecordsService {
	@Resource(name = "refundRecordsDaoImpl")
	RefundRecordsDao dao;

	@Resource(name = "refundRecordsDaoImpl")
	public void setBaseDao(RefundRecordsDao dao) {
		super.setBaseDao(dao);
	}
}