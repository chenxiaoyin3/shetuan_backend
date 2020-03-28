package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ShoppingCartDao;
import com.hongyu.entity.ShoppingCart;
@Repository("shoppingCartDaoImpl")
public class ShoppingCartDaoImpl extends BaseDaoImpl<ShoppingCart, Long> implements ShoppingCartDao {

}
