package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.Contact;
import com.hongyu.service.ContactService;

@Service("contactServiceImpl")
public class ContactServiceImpl extends BaseServiceImpl<Contact, Long> implements ContactService {

	@Override
	@Resource(name="contactDaoImpl")
	public void setBaseDao(BaseDao<Contact, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

	
}
