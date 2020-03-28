package com.hongyu.dao.impl;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.SpecialtyDao;
import com.hongyu.entity.Specialty;
import org.springframework.stereotype.Repository;

@Repository("specialtyDaoImpl")
public class SpecialtyDaoImpl
  extends BaseDaoImpl<Specialty, Long>
  implements SpecialtyDao
{}
