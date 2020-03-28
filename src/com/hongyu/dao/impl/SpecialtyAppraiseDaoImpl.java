package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.SpecialtyAppraiseDao;
import com.hongyu.entity.SpecialtyAppraise;

@Repository("specialtyAppraiseDaoImpl")
public class SpecialtyAppraiseDaoImpl extends BaseDaoImpl<SpecialtyAppraise, Long> implements SpecialtyAppraiseDao{

}
