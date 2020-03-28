package com.hongyu.service.impl;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.AddedServiceAndServiceTransferDao;
import com.hongyu.entity.AddedServiceAndServiceTransfer;
import com.hongyu.service.AddedServiceAndServiceTransferService;

@Service("addedServiceAndServiceTransferServiceImpl")
public class AddedServiceAndServiceTransferServiceImpl extends BaseServiceImpl<AddedServiceAndServiceTransfer, Long>
		implements AddedServiceAndServiceTransferService {
	@Resource(name = "addedServiceAndServiceTransferDaoImpl")
	AddedServiceAndServiceTransferDao dao;

	@Resource(name = "addedServiceAndServiceTransferDaoImpl")
	public void setBaseDao(AddedServiceAndServiceTransferDao dao) {
		super.setBaseDao(dao);
	}
}