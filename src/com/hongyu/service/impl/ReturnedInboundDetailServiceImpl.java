package com.hongyu.service.impl;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.ReturnedInboundDetailDao;
import com.hongyu.entity.ReturnedInboundDetail;
import com.hongyu.service.ReturnedInboundDetailService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service("returnedInboundDetailServiceImpl")
public class ReturnedInboundDetailServiceImpl
  extends BaseServiceImpl<ReturnedInboundDetail, Long>
  implements ReturnedInboundDetailService
{
  @Resource(name="returnedInboundDetailDaoImpl")
  ReturnedInboundDetailDao returnedInboundDetailDaoImpl;
  
  @Resource(name="returnedInboundDetailDaoImpl")
  public void setBaseDao(ReturnedInboundDetailDao dao)
  {
    super.setBaseDao(dao);
  }
}
