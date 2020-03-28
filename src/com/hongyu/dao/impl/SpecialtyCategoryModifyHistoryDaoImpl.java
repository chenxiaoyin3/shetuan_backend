package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.SpecialtyCategoryModifyHistoryDao;
import com.hongyu.entity.SpecialtyCategoryModifyHistory;

@Repository("specialtyCategoryModifyHistoryDaoImpl")
public class SpecialtyCategoryModifyHistoryDaoImpl 
extends BaseDaoImpl<SpecialtyCategoryModifyHistory, Long> 
implements SpecialtyCategoryModifyHistoryDao{

}
