package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.MhAppraiseDao;
import com.hongyu.dao.SpecialtyAppraiseDao;
import com.hongyu.entity.MhAppraise;
import com.hongyu.entity.SpecialtyAppraise;

@Repository("mhAppraiseDaoImpl")
public class MhAppraiseDaoImpl extends BaseDaoImpl<MhAppraise, Long> implements MhAppraiseDao{

}
