package com.hongyu.service.impl;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.SpecialtyDao;
import com.hongyu.entity.Specialty;
import com.hongyu.service.SpecialtyService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service("specialtyServiceImpl")
public class SpecialtyServiceImpl
  extends BaseServiceImpl<Specialty, Long>
  implements SpecialtyService
{
  @Resource(name="specialtyDaoImpl")
  SpecialtyDao specialtyDaoImpl;
  
  @Resource(name="specialtyDaoImpl")
  public void setBaseDao(SpecialtyDao dao)
  {
    super.setBaseDao(dao);
  }
}
