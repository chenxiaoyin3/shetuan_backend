package com.hongyu.service.impl;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.HyValidProduct;
import com.hongyu.service.HyValidProductService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("hyValidProductServiceImpl")
public class HyValidProductServiceImpl extends BaseServiceImpl<HyValidProduct,Long> implements HyValidProductService {

	@Resource(name="hyValidProductDaoImpl")
	public void setBaseDao(BaseDao dao){
		super.setBaseDao(dao);
	}
}
