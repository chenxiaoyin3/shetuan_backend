package com.shetuan.dao.impl;

import org.springframework.stereotype.Repository;
import com.grain.dao.impl.BaseDaoImpl;
import com.shetuan.entity.Organization;
import com.shetuan.dao.OrganizationDao;

@Repository("OrganizationDaoImpl")
public class OrganizationDaoImpl extends BaseDaoImpl<Organization,Long> implements OrganizationDao{
	
}
