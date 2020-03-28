package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.HyDimissionAuditProcess;
import com.hongyu.service.HyDimissionAuditProcessService;
@Service("hyDimissionAuditProcessServiceImpl")
public class HyDimissionAuditProcessServiceImpl extends BaseServiceImpl<HyDimissionAuditProcess, Long> implements HyDimissionAuditProcessService{
	@Override
	@Resource(name="hyDimissionAuditProcessDaoImpl")
	public void setBaseDao(BaseDao<HyDimissionAuditProcess, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
}
