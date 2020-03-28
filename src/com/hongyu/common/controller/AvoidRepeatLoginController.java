package com.hongyu.common.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.CommonAttributes;
import com.hongyu.Json;
import com.hongyu.SessionListener;

@RestController
@RequestMapping("/common/control/")
public class AvoidRepeatLoginController {
	
	@RequestMapping("avoidRepeatLogin")
	public Json  canLogin(HttpSession session,HttpServletRequest resquest,HttpServletResponse response){
		Json json = new Json();
		try{
			/** 已登录 (当前浏览器已经有登录了，不允许再次登陆)*/
			if (session.getAttribute(CommonAttributes.Principal) != null ){
			 	String username = (String) session.getAttribute(CommonAttributes.Principal);
		        HttpSession tempSession = SessionListener.sessionMap.get(username);
		        //如果session没有过期，就不允许访问登录界面
		        if (tempSession != null && tempSession.equals(session)) {
		        	throw new Exception("用户不能重复登录");
		        }
		        
			}
			json.setMsg("允许登录");
			json.setSuccess(true);
		}catch (Exception e) {
			json.setMsg("不允许访问登录页面："+e.getMessage());
			json.setSuccess(false);
		}
		return json;
	}
}
