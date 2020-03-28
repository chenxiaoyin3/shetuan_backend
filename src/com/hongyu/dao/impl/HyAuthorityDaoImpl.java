package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyAuthorityDao;
import com.hongyu.entity.HyAuthority;
@Repository("hyAuthorityDaoImpl")
public class HyAuthorityDaoImpl extends BaseDaoImpl<HyAuthority, Long> implements HyAuthorityDao {

}
