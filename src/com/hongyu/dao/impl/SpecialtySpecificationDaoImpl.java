package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.SpecialtySpecificationDao;
import com.hongyu.entity.SpecialtySpecification;

@Repository("specialtySpecificationDaoImpl")
public class SpecialtySpecificationDaoImpl extends BaseDaoImpl<SpecialtySpecification, Long> implements SpecialtySpecificationDao {

}
