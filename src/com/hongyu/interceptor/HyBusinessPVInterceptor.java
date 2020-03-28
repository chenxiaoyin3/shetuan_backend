package com.hongyu.interceptor;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.hongyu.entity.HyBusinessPV;
import com.hongyu.entity.WeBusiness;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.CaptchaService;
import com.hongyu.service.HyBusinessPVService;
import com.hongyu.service.WeBusinessService;
import com.hongyu.service.WechatAccountService;
import com.hongyu.util.Constants;
import com.sun.org.apache.bcel.internal.generic.NEW;

public class HyBusinessPVInterceptor implements HandlerInterceptor{
	@Resource(name = "wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;
	
	@Resource(name = "weBusinessServiceImpl")
	WeBusinessService weBusinessService;
	
	@Resource(name = "hyBusinessPVServiceImpl")
	HyBusinessPVService hyBusinessPVService;
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// TODO Auto-generated method stub
		
		//获取访问的URL
		String context = request.getContextPath();
		String targetURI = request.getRequestURI().replace(context + "/", "");
		
		HttpSession session = request.getSession();
		
		Long wechatId = (Long)session.getAttribute("wechat_id");
		Long webusinessId = (Long)session.getAttribute("webusiness_id");
		Integer type = null;
		Long itemId = null;
		switch(targetURI){
		case "ymmall/product/category/super_categories":	//首页
			type=Constants.PVClickType.HOME_PAGE.ordinal();
			break;
		case "ymmall/product/specification_detail_by_specialty_id":	//特产详情页
			type=Constants.PVClickType.SPECIALTY_DETAIL.ordinal();
			itemId=Long.valueOf(request.getParameter("id"));
			break;
		case "ymmall/promotion/normal/detail":	//普通优惠详情页
			type=Constants.PVClickType.SIMPLE_PROMOTION_DETAIL.ordinal();
			itemId=Long.valueOf(request.getParameter("id"));
			break;
		case "ymmall/promotion/group/detail":	//组合优惠详情页
			type=Constants.PVClickType.GROUP_PROMOTION_DETAIL.ordinal();
			itemId=Long.valueOf(request.getParameter("id"));
			break;
		default:
			return true;
		}
		
		WechatAccount wechatAccount = wechatAccountService.find(wechatId);
		WeBusiness weBusiness = weBusinessService.find(webusinessId);
		HyBusinessPV hyBusinessPV = new HyBusinessPV();
		hyBusinessPV.setClickTime(new Date());
		hyBusinessPV.setClickType(type);
		hyBusinessPV.setItemId(itemId);
		hyBusinessPV.setWeBusiness(weBusiness);
		hyBusinessPV.setWechatAccount(wechatAccount);
		hyBusinessPV.setIsValid(true);
		hyBusinessPVService.save(hyBusinessPV);
		return true;
		
	}

}
