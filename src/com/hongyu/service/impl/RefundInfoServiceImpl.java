package com.hongyu.service.impl;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.RefundInfoDao;
import com.hongyu.entity.RefundInfo;
import com.hongyu.service.RefundInfoService;

@Service("refundInfoServiceImpl")
public class RefundInfoServiceImpl extends BaseServiceImpl<RefundInfo, Long> implements RefundInfoService {
	@Resource(name = "refundInfoDaoImpl")
	RefundInfoDao dao;

	@Resource(name = "refundInfoDaoImpl")
	public void setBaseDao(RefundInfoDao dao) {
		super.setBaseDao(dao);
	}
}