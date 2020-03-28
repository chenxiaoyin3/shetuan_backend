package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.TwiceConsumeRecordDao;
import com.hongyu.entity.TwiceConsumeRecord;

@Repository("twiceConsumeRecordDaoImpl")
public class TwiceConsumeRecordDaoImpl extends BaseDaoImpl<TwiceConsumeRecord, Long> implements TwiceConsumeRecordDao {
}