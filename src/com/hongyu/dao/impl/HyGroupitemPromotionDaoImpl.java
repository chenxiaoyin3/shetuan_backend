package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyGroupitemPromotionDao;
import com.hongyu.entity.HyGroupitemPromotion;
@Repository("hyGroupitemPromotionDaoImpl")
public class HyGroupitemPromotionDaoImpl extends BaseDaoImpl<HyGroupitemPromotion, Long>
		implements HyGroupitemPromotionDao {

}
