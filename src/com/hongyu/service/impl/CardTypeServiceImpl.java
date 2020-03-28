package com.hongyu.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.CardTypeDao;
import com.hongyu.entity.CardType;
import com.hongyu.service.CardTypeService;
@Service(value = "cardTypeServiceImpl")
public class CardTypeServiceImpl extends BaseServiceImpl<CardType, Long> 
implements CardTypeService {

	
	@Resource(name = "cardTypeDaoImpl")
	CardTypeDao dao;
	
	@Resource(name = "cardTypeDaoImpl")
	public void setBaseDao(CardTypeDao dao){
		super.setBaseDao(dao);		
	}

	@Override
	public boolean checkName(CardType cardType) {
		boolean exist = false;
		List<CardType> s = dao.findList(null, null, null, null);
		
		for(CardType c : s){
			if(c.getName().equals(cardType.getName()) && cardType.getId()!=c.getId()){
				exist = true;
				break;
			}
		}
		return exist;
	}	
	
}
