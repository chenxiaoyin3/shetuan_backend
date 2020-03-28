package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyVisaDao;
import com.hongyu.entity.HyVisa;

@Repository("hyVisaDaoImpl")
public class HyVisaDaoImpl extends BaseDaoImpl<HyVisa, Long> implements HyVisaDao{

}
