package com.hongyu.service.impl;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.BalanceDueApplyItemDao;
import com.hongyu.entity.BalanceDueApplyItem;
import com.hongyu.service.BalanceDueApplyItemService;

@Service("balanceDueApplyItemServiceImpl")
public class BalanceDueApplyItemServiceImpl extends BaseServiceImpl<BalanceDueApplyItem, Long>
		implements BalanceDueApplyItemService {
	@Resource(name = "balanceDueApplyItemDaoImpl")
	BalanceDueApplyItemDao dao;

	@Resource(name = "balanceDueApplyItemDaoImpl")
	public void setBaseDao(BalanceDueApplyItemDao dao) {
		super.setBaseDao(dao);
	}
}