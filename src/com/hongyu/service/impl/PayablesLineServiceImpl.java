package com.hongyu.service.impl;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PayablesLineDao;
import com.hongyu.entity.PayablesLine;
import com.hongyu.service.PayablesLineService;

@Service("payablesLineServiceImpl")
public class PayablesLineServiceImpl extends BaseServiceImpl<PayablesLine, Long> implements PayablesLineService {
	@Resource(name = "payablesLineDaoImpl")
	PayablesLineDao dao;

	@Resource(name = "payablesLineDaoImpl")
	public void setBaseDao(PayablesLineDao dao) {
		super.setBaseDao(dao);
	}
}