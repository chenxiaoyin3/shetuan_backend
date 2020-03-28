package com.shetuan.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.shetuan.dao.JournalDao;
import com.shetuan.entity.Journal;

@Repository("JournalDaoImpl")
public class JournalDaoImpl extends BaseDaoImpl<Journal,Long> implements JournalDao{
	
}