package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.GuideAuditProcess;
import com.hongyu.service.GuideAuditProcessService;

@Service("guideAuditProcessServiceImpl")
public class GuideAuditProcessServiceImpl extends BaseServiceImpl<GuideAuditProcess, Long> implements GuideAuditProcessService {

	@Override
	@Resource(name="guideAuditProcessDaoImpl")
	public void setBaseDao(BaseDao<GuideAuditProcess, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
