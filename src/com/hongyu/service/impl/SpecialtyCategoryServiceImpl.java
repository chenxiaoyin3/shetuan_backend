package com.hongyu.service.impl;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.SpecialtyCategoryDao;
import com.hongyu.entity.SpecialtyCategory;
import com.hongyu.service.SpecialtyCategoryService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service("specialtyCategoryServiceImpl")
public class SpecialtyCategoryServiceImpl
  extends BaseServiceImpl<SpecialtyCategory, Long>
  implements SpecialtyCategoryService
{
  @Resource(name="specialtyCategoryDaoImpl")
  SpecialtyCategoryDao specialtyCategoryDaoImpl;
  
  @Resource(name="specialtyCategoryDaoImpl")
  public void setBaseDao(SpecialtyCategoryDao dao)
  {
    super.setBaseDao(dao);
  }
}
