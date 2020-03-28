package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.MhPwRefundRuleDao;
import com.hongyu.entity.MhPwRefundRule;
import com.hongyu.service.MhPwRefundRuleService;

@Service("mhPwRefundRuleServiceImpl")
public class MhPwRefundRuleServiceImpl extends BaseServiceImpl<MhPwRefundRule,Long> implements MhPwRefundRuleService {
	@Resource(name = "mhPwRefundRuleDaoImpl")
	MhPwRefundRuleDao dao;
	
	@Resource(name = "mhPwRefundRuleDaoImpl")
	public void setBaseDao(MhPwRefundRuleDao dao){
		super.setBaseDao(dao);		
	}	
}
