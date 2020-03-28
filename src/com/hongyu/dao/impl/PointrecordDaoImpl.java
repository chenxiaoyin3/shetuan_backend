package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.PointrecordDao;
import com.hongyu.entity.Pointrecord;

@Repository("pointrecordDaoImpl")
public class PointrecordDaoImpl extends BaseDaoImpl<Pointrecord,Long> implements PointrecordDao {

}
