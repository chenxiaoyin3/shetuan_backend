package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyPolicyHolderInfoDao;
import com.hongyu.entity.HyPolicyHolderInfo;
import com.hongyu.service.HyPolicyHolderInfoService;
@Service("hyPolicyHolderInfoServiceImpl")
public class HyPolicyHolderInfoServiceImpl extends BaseServiceImpl<HyPolicyHolderInfo, Long> implements HyPolicyHolderInfoService{
	@Resource(name = "hyPolicyHolderInfoDaoImpl")
	public void setBaseDao(HyPolicyHolderInfoDao dao){
		super.setBaseDao(dao);		
	}
}
