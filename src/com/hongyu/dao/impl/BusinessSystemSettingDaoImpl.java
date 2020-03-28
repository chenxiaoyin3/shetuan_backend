package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.BusinessSystemSettingDao;
import com.hongyu.entity.BusinessSystemSetting;

@Repository("businessSystemSettingDaoImpl")
public class BusinessSystemSettingDaoImpl extends BaseDaoImpl<BusinessSystemSetting, Long> implements BusinessSystemSettingDao {

}
