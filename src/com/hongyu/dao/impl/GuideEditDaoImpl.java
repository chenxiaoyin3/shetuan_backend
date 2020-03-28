package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.GuideEditDao;
import com.hongyu.entity.GuideEdit;
@Repository("guideEditDaoImpl")
public class GuideEditDaoImpl extends BaseDaoImpl<GuideEdit,Long> implements GuideEditDao{

}
