package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.WechatAccountDao;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.WechatAccountService;

@Service("wechatAccountServiceImpl")
public class WechatAccountServiceImpl extends BaseServiceImpl<WechatAccount, Long> implements WechatAccountService {
	  @Resource(name="wechatAccountDaoImpl")
	  WechatAccountDao wechatAccountDaoImpl;
	  
	  @Resource(name="wechatAccountDaoImpl")
	  public void setBaseDao(WechatAccountDao dao)
	  {
	    super.setBaseDao(dao);
	  }
}
