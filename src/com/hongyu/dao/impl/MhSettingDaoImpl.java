package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.BusinessSystemSettingDao;
import com.hongyu.dao.MhSettingDao;
import com.hongyu.entity.BusinessSystemSetting;
import com.hongyu.entity.MhSetting;

@Repository("mhSettingDaoImpl")
public class MhSettingDaoImpl extends BaseDaoImpl<MhSetting, Long> implements MhSettingDao {

}
