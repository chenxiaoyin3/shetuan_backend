package com.hongyu.service.impl;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.BusinessStoreDao;
import com.hongyu.entity.BusinessStore;
import com.hongyu.service.BusinessStoreService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service("businessStoreServiceImpl")
public class BusinessStoreServiceImpl
  extends BaseServiceImpl<BusinessStore, Long>
  implements BusinessStoreService
{
  @Resource(name="businessStoreDaoImpl")
  BusinessStoreDao businessStoreImpl;
  
  @Resource(name="businessStoreDaoImpl")
  public void setBaseDao(BusinessStoreDao dao)
  {
    super.setBaseDao(dao);
  }
}
