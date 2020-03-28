package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.WechatOfficialAccountDao;
import com.hongyu.entity.WechatOfficialAccount;
import com.hongyu.service.WechatOfficialAccountService;

@Service("wechatOfficialAccountServiceImpl")
public class WechatOfficialAccountServiceImpl extends BaseServiceImpl<WechatOfficialAccount, Long> implements WechatOfficialAccountService {

	@Resource(name="wechatOfficialAccountDaoImpl")
	WechatOfficialAccountDao wechatOfficialAccountDaoImpl;
	
	@Resource(name="wechatOfficialAccountDaoImpl")
	public void setBaseDao(WechatOfficialAccountDao dao){
		super.setBaseDao(dao);
	}
}
