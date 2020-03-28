package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ExposeDao;
import com.hongyu.entity.Expose;

@Repository("exposeDaoImpl")
public class ExposeDaoImpl extends BaseDaoImpl<Expose, Long> implements ExposeDao {

}
