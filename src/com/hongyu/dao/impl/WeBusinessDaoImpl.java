package com.hongyu.dao.impl;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.WeBusinessDao;
import com.hongyu.entity.WeBusiness;
import org.springframework.stereotype.Repository;

@Repository("weBusinessDaoImpl")
public class WeBusinessDaoImpl
  extends BaseDaoImpl<WeBusiness, Long>
  implements WeBusinessDao
{}
