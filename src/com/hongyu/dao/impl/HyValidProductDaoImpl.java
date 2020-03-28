package com.hongyu.dao.impl;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyValidProductDao;
import com.hongyu.entity.HyValidProduct;
import org.springframework.stereotype.Repository;

@Repository("hyValidProductDaoImpl")
public class HyValidProductDaoImpl extends BaseDaoImpl<HyValidProduct,Long> implements HyValidProductDao {
}
