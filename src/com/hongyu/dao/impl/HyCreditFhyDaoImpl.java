package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyCreditFhyDao;
import com.hongyu.entity.HyCreditFhy;

@Repository("hyCreditFhyDaoImpl")
public class HyCreditFhyDaoImpl extends BaseDaoImpl<HyCreditFhy,Long> implements HyCreditFhyDao {

}
