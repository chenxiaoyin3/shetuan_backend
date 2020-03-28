package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.RoleAuthorityDao;
import com.hongyu.entity.HyRoleAuthority;
@Repository("hyRoleAuthorityDaoImpl")
public class RoleAuthorityDaoImpl extends BaseDaoImpl<HyRoleAuthority, Long> 
implements RoleAuthorityDao {

}
