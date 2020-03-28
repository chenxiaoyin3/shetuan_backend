package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyDimissionAuditProcessDao;
import com.hongyu.entity.HyDimissionAuditProcess;

@Repository("hyDimissionAuditProcessDaoImpl")
public class HyDimissionAuditProcessDaoImpl extends BaseDaoImpl<HyDimissionAuditProcess, Long> implements HyDimissionAuditProcessDao{

}
