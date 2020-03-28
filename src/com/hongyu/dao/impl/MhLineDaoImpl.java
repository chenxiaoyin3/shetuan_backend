package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.MhLineDao;
import com.hongyu.entity.MhLine;
@Repository("mhLineDaoImpl")
public class MhLineDaoImpl extends BaseDaoImpl<MhLine, Long> implements MhLineDao{

}
