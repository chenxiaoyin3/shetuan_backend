package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ExposeReplyDao;
import com.hongyu.entity.ExposeReply;

@Repository("exposeReplyDaoImpl")
public class ExposeReplyDaoImpl extends BaseDaoImpl<ExposeReply, Long> implements ExposeReplyDao {

}
