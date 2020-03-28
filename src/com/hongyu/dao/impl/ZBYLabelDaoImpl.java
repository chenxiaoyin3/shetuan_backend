package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ZBYLabelDao;
import com.hongyu.entity.ZBYLabel;
@Repository("zbyLabelDaoImpl")
public class ZBYLabelDaoImpl extends BaseDaoImpl<ZBYLabel, Long> implements ZBYLabelDao{

}
