package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ContactGroupDao;
import com.hongyu.entity.ContactGroup;

@Repository("contactGroupDaoImpl")
public class ContactGroupDaoImpl extends BaseDaoImpl<ContactGroup, Long> implements ContactGroupDao{

}
