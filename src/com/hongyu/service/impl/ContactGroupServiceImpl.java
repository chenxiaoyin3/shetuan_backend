package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.ContactGroup;
import com.hongyu.service.ContactGroupService;

@Service("contactGroupServiceImpl")
public class ContactGroupServiceImpl extends BaseServiceImpl<ContactGroup, Long> implements ContactGroupService {

	@Override
	@Resource(name="contactGroupDaoImpl")
	public void setBaseDao(BaseDao<ContactGroup, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
	
}
