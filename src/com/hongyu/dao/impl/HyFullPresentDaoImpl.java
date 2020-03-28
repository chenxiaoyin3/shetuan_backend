package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyFullPresentDao;
import com.hongyu.entity.HyFullPresent;
@Repository("hyFullPresentDaoImpl")
public class HyFullPresentDaoImpl extends BaseDaoImpl<HyFullPresent, Long> implements HyFullPresentDao {

}
