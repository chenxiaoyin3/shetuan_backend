package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.BusinessSettingHistoryDao;
import com.hongyu.entity.BusinessSettingHistory;

@Repository("businessSettingHistoryDaoImpl")
public class BusinessSettingHistoryDaoImpl extends BaseDaoImpl<BusinessSettingHistory, Long> implements BusinessSettingHistoryDao {

}
