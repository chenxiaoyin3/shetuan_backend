package com.grain.interceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.hongyu.CommonAttributes;
import com.hongyu.Principal;

public class CommonLoginInterceptor implements HandlerInterceptor{
	
	
	private transient final Log log = LogFactory.getLog(this.getClass());
	// 根路径
	// 登陆表单路径
	private static String loginPath = "common/login";
	// 静态资源路径
	private static String resourcesPath = "resources";
	// 登陆提交路径
	private static String submitPath = "common/submit";
	// 注销
	private static String logoff = "common/logout";
	// 主页面
	private static String mainPath = "common/main";
	// 验证码
	private static String captchaPath = "common/captcha";
	//注册
	private static String registerPath = "common/register";
	
	private static String check = "common/check_username";
	
	private static String regexp = "common/addexp";
	
	private static String reginv = "common/addinv";
	
	private static String regfar = "common/addfar";
	
	private static String regfac = "common/addfac";
	


	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

	}

	/**
	 * 权限判断
	 */

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		boolean logdebug = false;
		if (log.isDebugEnabled()) {
			logdebug = true;
		}

		// 上下文路径
		String contextPath = request.getContextPath();
		// 访问路径
		String targetURI = request.getRequestURI().replace(contextPath + "/", "");
		HttpSession session = request.getSession();

		// 请求的是静态资源文件(如 .js .css 图片)或者注销，则直接通过
		if (targetURI.contains(resourcesPath) || targetURI.equals(logoff) 
				|| targetURI.equals(captchaPath)||targetURI.equals(mainPath)
				||targetURI.equals(registerPath)||targetURI.equals(check)||
				targetURI.equals(regfar)||targetURI.equals(regfac)||targetURI.equals(regexp)||targetURI.equals(reginv)) {
			return true;
		}
		
		return true;
//		// 未登录
//		if (session.getAttribute(CommonAttributes.Principal) == null) {
//			// 登陆路径与登陆表单不拦截
//			if (targetURI.equals(submitPath) || targetURI.equals(loginPath)) {
//				return true;
//			} else {
//				// 转到登陆页面
//				if (logdebug) {
//					log.debug("login is required!");
//				}
//				response.sendRedirect(contextPath + "/" + loginPath);
//				return false;
//			}
//			// 已登录
//		} else{
//			Principal principal = (Principal) request.getSession().getAttribute(CommonAttributes.Principal);
//			String username = principal.getUsername();
//			
//			return true;
//
//		}
	}

}
