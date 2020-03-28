package com.hongyu.service.impl;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.RegulategroupAccountDao;
import com.hongyu.entity.RegulategroupAccount;
import com.hongyu.service.RegulategroupAccountService;
@Service("regulategroupAccountServiceImpl")
public class RegulategroupAccountServiceImpl extends BaseServiceImpl<RegulategroupAccount, Long>
		implements RegulategroupAccountService {
	@Resource(name="regulategroupAccountDaoImpl")
	RegulategroupAccountDao regulategroupAccountDao;
	
	@Resource(name="regulategroupAccountDaoImpl")
	public void setBaseDao(RegulategroupAccountDao dao) {
		super.setBaseDao(dao);
	}
}
