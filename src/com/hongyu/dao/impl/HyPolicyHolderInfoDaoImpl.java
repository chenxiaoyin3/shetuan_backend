package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyPolicyHolderInfoDao;
import com.hongyu.entity.HyPolicyHolderInfo;
@Repository("hyPolicyHolderInfoDaoImpl")
public class HyPolicyHolderInfoDaoImpl extends BaseDaoImpl<HyPolicyHolderInfo, Long> implements HyPolicyHolderInfoDao{

}
