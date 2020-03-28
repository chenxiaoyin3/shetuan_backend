package com.hongyu.common.controller;

import static com.hongyu.util.Constants.loginPath;
import static com.hongyu.util.Constants.mainPath;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.grain.entity.user.Admin;
import com.grain.util.WebUtils;
import com.hongyu.CommonAttributes;
import com.hongyu.Json;
import com.hongyu.SessionListener;
import com.hongyu.controller.BaseController;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRole;
import com.hongyu.service.CaptchaService;
import com.hongyu.service.HyAdminService;
import com.hongyu.util.ForceLogoutUtils;

@RestController
@RequestMapping("/common")
public class HyLoginController extends BaseController{
	
	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "captchaServiceImpl")
	private CaptchaService captchaService;
	
	/**
	 * 获取验证码图片
	 * @param captchaId 验证码ID
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/captcha", method = RequestMethod.POST)
	public void image(String captchaId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (StringUtils.isEmpty(captchaId)) {
			captchaId = request.getSession().getId();
		}
		String pragma = new StringBuffer().append("yB").append("-").append("der").append("ewoP").reverse().toString();
		String value = new StringBuffer().append("ten").append(".").append("xxp").append("ohs").reverse().toString();
		response.addHeader(pragma, value);
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Cache-Control", "no-store");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");

		ServletOutputStream servletOutputStream = null;
		try {
			servletOutputStream = response.getOutputStream();
			BufferedImage bufferedImage = captchaService.buildImage(captchaId);
			ImageIO.write(bufferedImage, "jpg", servletOutputStream);
			servletOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(servletOutputStream);
		}
	}
	
	/**
	 * 登录提交
	 */
	@RequestMapping(value="/submit", method = RequestMethod.POST)
	public Json submit(/**String captchaId, String captcha,*/ String username, String password,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		
		Json j = new Json();
		
		/** 验证码错误，跳转到登录页面 */
//		if (!captchaService.isValid(captchaId,captcha)) {
//			try {
//				response.sendRedirect (loginPath);
//				j.setSuccess(false);
//				j.setMsg("验证码错误");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return j;
//		}
		HyAdmin admin = new HyAdmin();
		admin.setUsername(username);
		admin.setPassword(password);

		if (hyAdminService.loginCheck(username, password)) {
			
				//session.invalidate();
				try{
				session = request.getSession();
				//判断是否已经被登录
				if(null != SessionListener.sessionMap.get(admin.getUsername()) && session.isNew()) {
					ForceLogoutUtils.forceUserLogout(admin.getUsername() );
				}	
				
//				if(request.isRequestedSessionIdValid()) {
					SessionListener.sessionMap.put(admin.getUsername(), session);

					session.setAttribute(CommonAttributes.Principal, admin.getUsername());
					WebUtils.addCookie(request, response, "admin", admin.getUsername());
			
					j.setSuccess(true);
					j.setMsg("登录成功，转到主界面");
					
					
//				}
			
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				j.setMsg(e.getMessage());
			}
			return j;
		}
		j.setSuccess(false);
		j.setMsg("用户名或密码错误");
		return j;
	}
	
	/**
	 * 登出-跳转到登录页面
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public Json logout(HttpSession session, HttpServletResponse response){
		Json j = new Json();
		try {
			String admin = (String) session.getAttribute(CommonAttributes.Principal);
			SessionListener.sessionMap.remove(admin);
			session.removeAttribute(CommonAttributes.Principal);
			session.invalidate();
			
			j.setSuccess(true);
			j.setMsg("登出成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return j;
	}
	/**
	 * 避免重复登陆
	 * @param session
	 * @param resquest
	 * @param response
	 * @return
	 */
	@RequestMapping("avoidRepeatLogin")
	public Json  canLogin(HttpSession session,HttpServletRequest resquest,HttpServletResponse response){
		Json json = new Json();
		try{
			boolean result = true;
			/** 已登录 (当前浏览器已经有登录了，不允许再次登陆)*/
			if (session.getAttribute(CommonAttributes.Principal) != null ){
			 	String username = (String) session.getAttribute(CommonAttributes.Principal);
		        HttpSession tempSession = SessionListener.sessionMap.get(username);
		        //如果session没有过期，就不允许访问登录界面
		        if (tempSession != null && tempSession.equals(session)) {
		        	result = false;
		        }        
			}
			json.setMsg("获取成功");
			json.setSuccess(true);
			json.setObj(result);
		}catch (Exception e) {
			json.setMsg("获取失败："+e.getMessage());
			json.setSuccess(false);
		}
		return json;
	}
}
