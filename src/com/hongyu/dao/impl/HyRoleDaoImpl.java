package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyRoleDao;
import com.hongyu.entity.HyRole;
@Repository("hyRoleDaoImpl")
public class HyRoleDaoImpl extends BaseDaoImpl<HyRole, Long> implements HyRoleDao {

}
