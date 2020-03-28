package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyDepartmentModelDao;
import com.hongyu.entity.HyDepartmentModel;
@Repository("hyDepartmentModelDaoImpl")
public class HyDepartmentModelDaoImpl extends BaseDaoImpl<HyDepartmentModel, String> 
implements HyDepartmentModelDao{

}
