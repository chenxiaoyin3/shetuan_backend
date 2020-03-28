package com.hongyu.service.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.dao.HyAdminDao;
import com.hongyu.dao.HyDistributorPrechargeRecordDao;
import com.hongyu.entity.HyDistributorPrechargeRecord;
import com.hongyu.service.HyDistributorPrechargeRecordService;

@Service("hyDistributorPrechargeRecordServiceImpl")
public class HyDistributorPrechargeRecordServiceImpl extends BaseServiceImpl<HyDistributorPrechargeRecord,Long>
       implements HyDistributorPrechargeRecordService {
	@Resource(name = "hyDistributorPrechargeRecordDaoImpl")
	HyDistributorPrechargeRecordDao dao;
	
	@Resource(name = "hyAdminDaoImpl")
	HyAdminDao adminDao;
	
	@Resource(name = "hyDistributorPrechargeRecordDaoImpl")
	public void setBaseDao(HyDistributorPrechargeRecordDao dao){
		super.setBaseDao(dao);	
	}
	
	@Override
	public void save(HyDistributorPrechargeRecord hyDistributorPrechargeRecord){
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();	
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			String username = (String) request.getSession().getAttribute(CommonAttributes.Principal);
			if (username != null)
				hyDistributorPrechargeRecord.setOperator(adminDao.find(username));
		}
		super.save(hyDistributorPrechargeRecord);
	}
}
