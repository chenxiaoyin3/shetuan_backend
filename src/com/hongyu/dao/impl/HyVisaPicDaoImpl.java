package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyVisaPicDao;
import com.hongyu.entity.HyVisaPic;

@Repository("hyVisaPicDaoImpl")
public class HyVisaPicDaoImpl extends BaseDaoImpl<HyVisaPic,Long> implements HyVisaPicDao {

}
