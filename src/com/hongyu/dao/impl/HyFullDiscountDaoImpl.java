package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyFullDiscountDao;
import com.hongyu.entity.HyFullDiscount;
@Repository("hyFullDiscountDaoImpl")
public class HyFullDiscountDaoImpl extends BaseDaoImpl<HyFullDiscount, Long> implements HyFullDiscountDao {

}
