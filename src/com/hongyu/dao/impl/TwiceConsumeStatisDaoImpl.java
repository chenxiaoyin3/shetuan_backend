package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.TwiceConsumeStatisDao;
import com.hongyu.entity.TwiceConsumeStatis;

@Repository("twiceConsumeStatisDaoImpl")
public class TwiceConsumeStatisDaoImpl extends BaseDaoImpl<TwiceConsumeStatis, Long> implements TwiceConsumeStatisDao {
}