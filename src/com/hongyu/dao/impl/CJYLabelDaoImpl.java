package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.CJYLabelDao;
import com.hongyu.entity.CJYLabel;
@Repository("cjyLabelDaoImpl")
public class CJYLabelDaoImpl extends BaseDaoImpl<CJYLabel, Long> implements CJYLabelDao{

}
