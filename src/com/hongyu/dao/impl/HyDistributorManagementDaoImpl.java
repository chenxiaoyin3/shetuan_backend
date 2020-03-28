package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyDistributorManagementDao;
import com.hongyu.entity.HyDistributorManagement;

@Repository("hyDistributorManagementDaoImpl")
public class HyDistributorManagementDaoImpl extends BaseDaoImpl<HyDistributorManagement,Long> implements HyDistributorManagementDao {

}
