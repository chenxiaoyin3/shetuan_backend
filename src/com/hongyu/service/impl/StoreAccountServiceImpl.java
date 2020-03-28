package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.StoreAccount;
import com.hongyu.service.StoreAccountService;

@Service("storeAccountServiceImpl")
public class StoreAccountServiceImpl extends BaseServiceImpl<StoreAccount,Long> implements StoreAccountService {

	@Resource(name="storeAccountDaoImpl")
	@Override
	public void setBaseDao(BaseDao<StoreAccount, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
