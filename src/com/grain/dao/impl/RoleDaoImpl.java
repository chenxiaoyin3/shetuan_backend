package com.grain.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.RoleDao;
import com.grain.entity.user.Role;

/**
 * Dao - 角色
 * 
 */
@Repository("roleDaoImpl")
public class RoleDaoImpl extends BaseDaoImpl<Role, Long> implements RoleDao {

}