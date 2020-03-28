package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyDistributorPrechargeRecordDao;
import com.hongyu.entity.HyDistributorPrechargeRecord;

@Repository("hyDistributorPrechargeRecordDaoImpl")
public class HyDistributorPrechargeRecordDaoImpl extends BaseDaoImpl<HyDistributorPrechargeRecord,Long>
 implements HyDistributorPrechargeRecordDao{

}
