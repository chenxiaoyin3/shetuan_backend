package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyPromotionPicDao;
import com.hongyu.entity.HyPromotionPic;
@Repository("hyPromotionPicDaoImpl")
public class HyPromotionPicDaoImpl extends BaseDaoImpl<HyPromotionPic, Long> implements HyPromotionPicDao {

}
