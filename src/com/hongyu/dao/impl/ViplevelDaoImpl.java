package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ViplevelDao;
import com.hongyu.entity.Viplevel;

@Repository("viplevelDaoImpl")
public class ViplevelDaoImpl extends BaseDaoImpl<Viplevel,Long> implements ViplevelDao {

}
