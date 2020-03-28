package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HySingleitemPromotionDao;
import com.hongyu.entity.HySingleitemPromotion;
@Repository("hySingleitemPromotionDaoImpl")
public class HySingleitemPromotionController extends BaseDaoImpl<HySingleitemPromotion, Long>
		implements HySingleitemPromotionDao {

}
