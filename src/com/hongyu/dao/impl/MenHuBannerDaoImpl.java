package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.BusinessBannerDao;
import com.hongyu.dao.MenHuBannerDao;
import com.hongyu.entity.BusinessBanner;
import com.hongyu.entity.MenHuBanner;


@Repository("menHuBannerDaoImpl")
public class MenHuBannerDaoImpl extends BaseDaoImpl<MenHuBanner, Long> implements MenHuBannerDao {

}
