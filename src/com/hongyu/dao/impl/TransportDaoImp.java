package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyLineTransportDao;
import com.hongyu.entity.TransportEntity;
@Repository("transportDaoImp")
public class TransportDaoImp extends BaseDaoImpl<TransportEntity, Long> implements HyLineTransportDao {

}
