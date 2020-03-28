package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.BusinessBannerDao;
import com.hongyu.entity.BusinessBanner;


@Repository("businessBannerDaoImpl")
public class BusinessBannerDaoImpl extends BaseDaoImpl<BusinessBanner, Long> implements BusinessBannerDao {

}
