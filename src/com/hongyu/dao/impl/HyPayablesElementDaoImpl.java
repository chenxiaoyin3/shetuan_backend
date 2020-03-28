package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyPayablesElementDao;
import com.hongyu.entity.HyPayablesElement;

@Repository("hyPayablesElementDaoImpl")
public class HyPayablesElementDaoImpl extends BaseDaoImpl<HyPayablesElement,Long> implements HyPayablesElementDao {

}
