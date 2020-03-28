package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.HyDimissionAudit;
import com.hongyu.service.HyDimissionAuditService;

@Service("hyDimissonAuditServiceImpl")
public class HyDimissionAuditServiceImpl extends BaseServiceImpl<HyDimissionAudit, Long> implements HyDimissionAuditService{
	@Override
	@Resource(name="hyDimissionAuditDaoImpl")
	public void setBaseDao(BaseDao<HyDimissionAudit, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
}
