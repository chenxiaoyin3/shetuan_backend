package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.WechatOfficialAccountDao;
import com.hongyu.entity.WechatOfficialAccount;

@Repository("wechatOfficialAccountDaoImpl")
public class WechatOfficialAccountDaoImpl  extends BaseDaoImpl<WechatOfficialAccount,Long> implements WechatOfficialAccountDao{

}
