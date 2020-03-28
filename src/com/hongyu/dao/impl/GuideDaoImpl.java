package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.GuideDao;
import com.hongyu.entity.Guide;

@Repository("guideDaoImpl")
public class GuideDaoImpl extends BaseDaoImpl<Guide, Long> implements GuideDao {

}
