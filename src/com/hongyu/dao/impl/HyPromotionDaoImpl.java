package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyPromotionDao;
import com.hongyu.entity.HyPromotion;

@Repository("hyPromotionDaoImpl")
public class HyPromotionDaoImpl extends BaseDaoImpl<HyPromotion, Long> implements HyPromotionDao {

}
