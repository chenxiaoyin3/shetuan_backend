package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.dao.FhyStoreRechargeDao;
import com.hongyu.dao.FinishedGroupItemOrderDao;
import com.hongyu.entity.FinishedGroupItemOrder;
import com.hongyu.entity.RegulategroupAccount;
import com.hongyu.service.FinishedGroupItemOrderService;
@Service("finishedGroupItemOrderServiceImpl")
public class FinishedGroupItemOrderServiceImpl extends BaseServiceImpl<FinishedGroupItemOrder, Long> implements FinishedGroupItemOrderService{
	@Resource(name = "finishedGroupItemOrderDaoImpl")
	 FinishedGroupItemOrderDao dao;

	@Resource(name = "finishedGroupItemOrderDaoImpl")
	public void setBaseDao(FinishedGroupItemOrderDao dao) {
		super.setBaseDao(dao);
	}
	
	@Override
	public void updateGroupItemOrder(RegulategroupAccount account, Long groupId) throws Exception {
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.eq("groupId", groupId));
		List<FinishedGroupItemOrder> itemOrders = this.findList(null,filters,null);
		BigDecimal dantuanshouru = account.getAllIncome();
		BigDecimal dantuanchengben = account.getAllExpense();
		BigDecimal dantuanlirun = account.getProfit();
		BigDecimal renjunlirun = account.getAverageProfit();
		BigDecimal lirunlv = dantuanlirun.divide(dantuanchengben,8,RoundingMode.CEILING);
		for(FinishedGroupItemOrder fgio:itemOrders){
			fgio.setGroupIncome(dantuanshouru);
			fgio.setGroupExpend(dantuanchengben);
			fgio.setGroupProfit(dantuanlirun);
			fgio.setGroupAverageProfit(renjunlirun);
			fgio.setGroupProfitMargin(lirunlv);
			this.save(fgio);
		}
	}
	
}
