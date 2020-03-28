package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyDimissionAuditDao;
import com.hongyu.entity.HyDimissionAudit;
@Repository("hyDimissionAuditDaoImpl")
public class HyDimissionAuditDaoImpl extends BaseDaoImpl<HyDimissionAudit, Long> implements HyDimissionAuditDao{

}
