package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyProviderRebateDao;
import com.hongyu.entity.HyProviderRebate;

@Repository("hyProviderRebateDaoImpl")
public class HyProviderRebateDaoImpl extends BaseDaoImpl<HyProviderRebate, Long> implements HyProviderRebateDao {

}
