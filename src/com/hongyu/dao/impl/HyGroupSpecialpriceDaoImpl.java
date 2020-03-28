package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyGroupSpecialpriceDao;
import com.hongyu.entity.HyGroupSpecialprice;
@Repository("hyGroupSpecialpriceDaoImpl")
public class HyGroupSpecialpriceDaoImpl extends BaseDaoImpl<HyGroupSpecialprice, Long>
		implements HyGroupSpecialpriceDao {

}
