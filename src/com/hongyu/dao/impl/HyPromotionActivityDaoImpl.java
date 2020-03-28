package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyPromotionActivityDao;
import com.hongyu.entity.HyPromotionActivity;

@Repository("hyPromotionActivityDaoImpl")
public class HyPromotionActivityDaoImpl extends BaseDaoImpl<HyPromotionActivity, Long> implements HyPromotionActivityDao {

}
