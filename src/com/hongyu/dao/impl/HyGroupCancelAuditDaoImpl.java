package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyGroupCancelAuditDao;
import com.hongyu.entity.HyGroupCancelAudit;

@Repository("hyGroupCancelAuditDaoImpl")
public class HyGroupCancelAuditDaoImpl extends BaseDaoImpl<HyGroupCancelAudit,Long> implements HyGroupCancelAuditDao {

}
