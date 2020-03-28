package com.hongyu.service;

import com.grain.service.BaseService;
import com.hongyu.entity.CardType;

public interface CardTypeService extends BaseService<CardType, Long> {
	boolean checkName(CardType cardType);
}
