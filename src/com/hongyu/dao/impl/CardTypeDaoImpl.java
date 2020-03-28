package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.CardTypeDao;
import com.hongyu.entity.CardType;
@Repository("cardTypeDaoImpl")
public class CardTypeDaoImpl extends BaseDaoImpl<CardType, Long> 
implements CardTypeDao {

}
