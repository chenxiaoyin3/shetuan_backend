package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.GroupTypeDao;
import com.hongyu.entity.GroupType;
@Repository("groupTypeDaoImpl")
public class GroupTypeDaoImpl extends BaseDaoImpl<GroupType, Long> 
implements GroupTypeDao {

}
