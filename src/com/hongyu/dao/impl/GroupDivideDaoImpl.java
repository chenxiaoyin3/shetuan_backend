package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.GroupDivideDao;
import com.hongyu.entity.GroupDivide;

@Repository("groupDivideDaoImpl")
public class GroupDivideDaoImpl extends BaseDaoImpl<GroupDivide, Long> implements GroupDivideDao{

}
