package com.hongyu.service.impl;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PayablesRefundItemDao;
import com.hongyu.entity.PayablesRefundItem;
import com.hongyu.service.PayablesRefundItemService;

@Service("payablesRefundItemServiceImpl")
public class PayablesRefundItemServiceImpl extends BaseServiceImpl<PayablesRefundItem, Long>
		implements PayablesRefundItemService {
	@Resource(name = "payablesRefundItemDaoImpl")
	PayablesRefundItemDao dao;

	@Resource(name = "payablesRefundItemDaoImpl")
	public void setBaseDao(PayablesRefundItemDao dao) {
		super.setBaseDao(dao);
	}
}