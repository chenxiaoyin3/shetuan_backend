package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyOrderApplicationItemDao;
import com.hongyu.entity.HyOrderApplicationItem;

@Repository("hyOrderApplicationItemDaoImpl")
public class HyOrderApplicationItemDaoImpl extends BaseDaoImpl<HyOrderApplicationItem, Long> implements HyOrderApplicationItemDao {

}
