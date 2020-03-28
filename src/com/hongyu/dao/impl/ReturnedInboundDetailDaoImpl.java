package com.hongyu.dao.impl;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ReturnedInboundDetailDao;
import com.hongyu.entity.ReturnedInboundDetail;
import org.springframework.stereotype.Repository;

@Repository("returnedInboundDetailDaoImpl")
public class ReturnedInboundDetailDaoImpl
  extends BaseDaoImpl<ReturnedInboundDetail, Long>
  implements ReturnedInboundDetailDao
{}
