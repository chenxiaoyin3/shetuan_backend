package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.LinePromotionDao;
import com.hongyu.entity.LinePromotion;

@Repository("linePromotionDaoImpl")
public class LinePromotionDaoImpl extends BaseDaoImpl<LinePromotion, Long> implements LinePromotionDao {

}
