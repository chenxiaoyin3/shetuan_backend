package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyUserDao;
import com.hongyu.entity.HyUser;

@Repository("hyUserDaoImpl")
public class HyUserDaoImpl extends BaseDaoImpl<HyUser, Long> implements HyUserDao {

}
