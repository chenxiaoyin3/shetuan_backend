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
import com.hongyu.dao.WeDivideProportionDao;
import com.hongyu.entity.HyPromotion;
import com.hongyu.entity.WeDivideProportion;
import com.hongyu.service.WeDivideProportionService;

@Service("weDivideProportionServiceImpl")
public class WeDivideProportionServiceImpl extends BaseServiceImpl<WeDivideProportion, Long>
implements WeDivideProportionService{
	@Resource(name="weDivideProportionDaoImpl")
	WeDivideProportionDao weDivideProportionDaoImpl;
	
	@Resource(name = "hyAdminDaoImpl")
	HyAdminDao adminDao;
	  
	  @Resource(name="weDivideProportionDaoImpl")
	  public void setBaseDao(WeDivideProportionDao dao)
	  {
	    super.setBaseDao(dao);
	  }
	  
		@Override
		public void save(WeDivideProportion weDivideProportion)  {
			RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
			if (requestAttributes != null) {
				HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
				String username = (String) request.getSession().getAttribute(CommonAttributes.Principal);
				if (username != null)
					weDivideProportion.setOperator(adminDao.find(username));
			}
			super.save(weDivideProportion);
		}
}
