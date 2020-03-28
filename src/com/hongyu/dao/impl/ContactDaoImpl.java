package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ContactDao;
import com.hongyu.entity.Contact;

@Repository("contactDaoImpl")
public class ContactDaoImpl extends BaseDaoImpl<Contact, Long> implements ContactDao{

}
