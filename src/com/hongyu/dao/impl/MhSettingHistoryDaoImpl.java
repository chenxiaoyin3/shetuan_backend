package com.hongyu.dao.impl;
import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.MhSettingDao;
import com.hongyu.dao.MhSettingHistoryDao;
import com.hongyu.entity.MhSetting;
import com.hongyu.entity.MhSettingHistory;

@Repository("mhSettingHistoryDaoImpl")
public class MhSettingHistoryDaoImpl extends BaseDaoImpl<MhSettingHistory, Long> implements MhSettingHistoryDao {

}
