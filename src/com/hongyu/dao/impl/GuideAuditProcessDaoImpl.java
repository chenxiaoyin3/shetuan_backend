package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.GuideAuditProcessDao;
import com.hongyu.entity.GuideAuditProcess;

@Repository("guideAuditProcessDaoImpl")
public class GuideAuditProcessDaoImpl extends BaseDaoImpl<GuideAuditProcess, Long> implements GuideAuditProcessDao {

}
