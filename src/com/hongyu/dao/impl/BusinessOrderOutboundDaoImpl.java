package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.BusinessOrderOutboundDao;
import com.hongyu.entity.BusinessOrderOutbound;
@Repository("businessOrderOutboundDaoImpl")
public class BusinessOrderOutboundDaoImpl extends BaseDaoImpl<BusinessOrderOutbound, Long>
		implements BusinessOrderOutboundDao {

}
