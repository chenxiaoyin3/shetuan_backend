package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.ShoppingCart;
import com.hongyu.service.ShoppingCartService;
@Service("shoppingCartServiceImpl")
public class ShoppingCartServiceImpl extends BaseServiceImpl<ShoppingCart, Long> implements ShoppingCartService{

	@Override
	@Resource(name="shoppingCartDaoImpl")
	public void setBaseDao(BaseDao<ShoppingCart, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
