package com.hongyu.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.util.WebUtils;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.WeBusiness;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.WeBusinessService;
import com.hongyu.service.WechatAccountService;
import com.hongyu.service.impl.WeBusinessServiceImpl;

@Controller
@RequestMapping("/ymmall/login")
public class YmmallLoginController {
	
	@Resource(name = "wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;
	
	@Resource(name = "weBusinessServiceImpl")
	WeBusinessService weBusinessService;
	
	@RequestMapping(value={"/submit"},method = RequestMethod.POST)
	@ResponseBody
	public Json submit(/**String captchaId, String captcha,*/ 
			@RequestParam(value="wechat_openid")String wechatOpenid,
			@RequestParam(value="wechat_name")String wechatName,
			@RequestParam(value="webusiness_id")Long webusinessId,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		
		Json j = new Json();
		
		/** 验证码错误，跳转到登录页面 */
//		if (!captchaService.isValid(captchaId,captcha)) {
//			try {
//				response.sendRedirect(loginPath);
//				j.setSuccess(false);
//				j.setMsg("验证码错误");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return j;
//		}\
		if(wechatOpenid==null || webusinessId == null) {
			j.setSuccess(false);
			j.setMsg("登录失败");
			j.setObj("微商id或微信openid不能为空");
			return j;
		}
		
		
		
		
		
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.eq("wechatOpenid", wechatOpenid));

		List<WechatAccount> wechatAccounts = wechatAccountService.findList(null,filters,null);
		String msg = "1 ";
		if(wechatAccounts == null || wechatAccounts.isEmpty()){
			WechatAccount wechatAccount = new WechatAccount();
			wechatAccount.setWechatName(wechatName);
			wechatAccount.setWechatOpenid(wechatOpenid);
			
			List<Filter> filters1 = new ArrayList<>();
			filters1.add(Filter.eq("wechatOpenId", wechatOpenid));
			List<WeBusiness> weBusiness = weBusinessService.findList(null,filters1,null);
			wechatAccount.setIsWeBusiness(weBusiness!=null && !weBusiness.isEmpty());

			wechatAccountService.save(wechatAccount);
			
			wechatAccounts.add(wechatAccount);
		}
		
		WechatAccount wechatAccount = wechatAccounts.get(0);
		
		List<Filter> filters2 = new ArrayList<>();
		filters2.add(Filter.eq("wechatOpenId", wechatOpenid));
		List<WeBusiness> weBusiness = weBusinessService.findList(null,filters2,null);
		if(weBusiness!=null && !weBusiness.isEmpty())
			wechatAccount.setCustomer_uid(weBusiness.get(0).getId());
		
		if(wechatAccount != null){
			//session.invalidate();
			//session = request.getSession();
			session.setAttribute("webusiness_id", webusinessId);
			session.setAttribute("wechat_id", wechatAccount.getId());
			WebUtils.addCookie(request, response, "wechat_id", wechatAccount.getId().toString());
			WebUtils.addCookie(request, response, "webusiness_id", webusinessId.toString());
			try {
				j.setSuccess(true);
				j.setMsg("登录成功");
				j.setObj(wechatAccount);
				return j;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				j.setSuccess(false);
				j.setMsg("登录失败");
				return j;
			}
		}
		j.setSuccess(false);
		j.setMsg("登录失败");
		return j;
		
	}

}
