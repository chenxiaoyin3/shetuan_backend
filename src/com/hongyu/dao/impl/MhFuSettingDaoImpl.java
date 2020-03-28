package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.MhFuSettingDao;
import com.hongyu.entity.MhFuSetting;

@Repository("mhFuSettingDaoImpl")
public class MhFuSettingDaoImpl extends BaseDaoImpl<MhFuSetting, Long> implements MhFuSettingDao {

}
