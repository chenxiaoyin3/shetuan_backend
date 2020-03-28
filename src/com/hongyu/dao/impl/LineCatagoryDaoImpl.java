package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.LineCatagoryDao;
import com.hongyu.entity.LineCatagoryEntity;
@Repository("lineCatagoryDaoImpl")
public class LineCatagoryDaoImpl extends BaseDaoImpl<LineCatagoryEntity, Long> implements LineCatagoryDao {

}
