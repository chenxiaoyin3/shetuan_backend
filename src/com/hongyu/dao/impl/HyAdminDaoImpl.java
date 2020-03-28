package com.hongyu.dao.impl;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyAdminDao;
import com.hongyu.entity.HyAdmin;
@Repository("hyAdminDaoImpl")
public class HyAdminDaoImpl extends BaseDaoImpl<HyAdmin, String> implements HyAdminDao {
	
}
