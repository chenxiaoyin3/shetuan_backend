package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyLineDao;
import com.hongyu.entity.HyLine;
@Repository("hyLineDaoImpl")
public class HyLineDaoImpl extends BaseDaoImpl<HyLine, Long> implements HyLineDao {

}
