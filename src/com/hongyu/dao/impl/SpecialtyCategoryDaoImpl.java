package com.hongyu.dao.impl;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.SpecialtyCategoryDao;
import com.hongyu.entity.SpecialtyCategory;
import org.springframework.stereotype.Repository;

@Repository("specialtyCategoryDaoImpl")
public class SpecialtyCategoryDaoImpl
  extends BaseDaoImpl<SpecialtyCategory, Long>
  implements SpecialtyCategoryDao
{}
