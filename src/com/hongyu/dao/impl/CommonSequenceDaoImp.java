package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.CommonSequenceDao;
import com.hongyu.entity.CommonSequence;
@Repository("commonSequenceDaoImp")
public class CommonSequenceDaoImp extends BaseDaoImpl<CommonSequence, Long> implements CommonSequenceDao {

}
