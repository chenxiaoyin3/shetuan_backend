package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.DistributorPreSaveDao;
import com.hongyu.entity.DistributorPreSave;

@Repository("distributorPreSaveDaoImpl")
public class DistributorPreSaveDaoImpl extends BaseDaoImpl<DistributorPreSave, Long> implements DistributorPreSaveDao {

}
