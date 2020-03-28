package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.MendianAuthorityDao;
import com.hongyu.entity.MendianAuthority;
@Repository("mendianAuthorityDaoImpl")
public class MendianAuthorityDaoImpl extends BaseDaoImpl<MendianAuthority, Long> implements MendianAuthorityDao {

}
