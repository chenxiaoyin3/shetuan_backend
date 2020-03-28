package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.AssistDao;
import com.hongyu.entity.Assist;

@Repository("assistDaoImpl")
public class AssistDaoImpl extends BaseDaoImpl<Assist, Long> implements AssistDao{

}
