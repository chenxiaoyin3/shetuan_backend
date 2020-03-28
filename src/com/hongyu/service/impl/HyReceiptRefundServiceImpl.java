package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.HyReceiptRefund;
import com.hongyu.service.HyReceiptRefundService;

@Service("hyReceiptRefundServiceImpl")
public class HyReceiptRefundServiceImpl extends BaseServiceImpl<HyReceiptRefund, Long>
		implements HyReceiptRefundService {
	
	@Resource(name="hyReceiptRefundDaoImpl")
	public void setBaseDao(BaseDao dao){
		super.setBaseDao(dao);
	}
	
	

}
