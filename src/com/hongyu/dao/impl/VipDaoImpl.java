package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.VipDao;
import com.hongyu.entity.Vip;

@Repository("vipDaoImpl")
public class VipDaoImpl extends BaseDaoImpl<Vip,Long> implements VipDao {

}
