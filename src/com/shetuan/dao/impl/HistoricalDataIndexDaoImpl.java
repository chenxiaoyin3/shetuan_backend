package com.shetuan.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.shetuan.dao.HistoricalDataIndexDao;
import com.shetuan.entity.HistoricalDataIndex;

@Repository("HistoricalDataIndexDaoImpl")
public class HistoricalDataIndexDaoImpl extends BaseDaoImpl<HistoricalDataIndex,Long> implements HistoricalDataIndexDao{
	
}