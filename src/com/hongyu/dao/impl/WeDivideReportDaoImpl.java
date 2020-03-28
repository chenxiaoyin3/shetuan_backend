package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.WeDivideReportDao;
import com.hongyu.entity.WeDivideReport;

@Repository("weDivideReportDaoImpl")
public class WeDivideReportDaoImpl extends BaseDaoImpl<WeDivideReport, Long> implements WeDivideReportDao {

}
