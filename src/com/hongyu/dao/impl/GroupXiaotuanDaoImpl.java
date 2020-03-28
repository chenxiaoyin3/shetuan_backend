package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.GroupXiaotuanDao;
import com.hongyu.entity.GroupXiaotuan;
@Repository("groupXiaotuanDaoImpl")
public class GroupXiaotuanDaoImpl extends BaseDaoImpl<GroupXiaotuan, Long> implements GroupXiaotuanDao {

}
