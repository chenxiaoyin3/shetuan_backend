package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.WechatAccountDao;
import com.hongyu.entity.WechatAccount;

@Repository("wechatAccountDaoImpl")
public class WechatAccountDaoImpl extends BaseDaoImpl<WechatAccount, Long> implements WechatAccountDao {

}
