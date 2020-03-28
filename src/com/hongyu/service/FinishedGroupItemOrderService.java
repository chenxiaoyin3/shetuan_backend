package com.hongyu.service;

import com.grain.service.BaseService;
import com.hongyu.entity.FinishedGroupItemOrder;
import com.hongyu.entity.RegulategroupAccount;

public interface FinishedGroupItemOrderService extends BaseService<FinishedGroupItemOrder, Long>{
	/**
	 * （内部供应商）单团核算表更新的时候，需要更新单团利润表
	 * @param account
	 * @param groupId
	 * @throws Exception
	 */
	void updateGroupItemOrder(RegulategroupAccount account,Long groupId) throws Exception;
}
