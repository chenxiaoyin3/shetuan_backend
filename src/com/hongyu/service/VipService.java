package com.hongyu.service;

import java.math.BigDecimal;

import com.grain.service.BaseService;
import com.hongyu.entity.Vip;
import com.hongyu.entity.Viplevel;
import com.hongyu.entity.WechatAccount;

public interface VipService extends BaseService<Vip,Long> {
	
	Viplevel getViplevelByWechatAccountId(Long id) throws Exception;
	
	void setVip318(WechatAccount wechatAccount,BigDecimal money);

}
