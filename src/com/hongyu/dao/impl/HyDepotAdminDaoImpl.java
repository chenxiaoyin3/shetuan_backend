package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyDepotAdminDao;
import com.hongyu.entity.HyDepotAdmin;


@Repository("hyDepotAdminDaoImpl")
public class HyDepotAdminDaoImpl extends BaseDaoImpl<HyDepotAdmin, Long> implements HyDepotAdminDao {

}
