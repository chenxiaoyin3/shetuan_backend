package com.grain.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.grain.service.user.AdminService;
import com.hongyu.CommonAttributes;
import com.hongyu.Principal;

/**
 * @author szy 权限验证拦截器 管理员权限拦截器
 */

public class AdminInterceptor implements HandlerInterceptor {
	private transient final Log log = LogFactory.getLog(this.getClass());
	// 根路径
	private static String rootPath = "admin";
	// 登陆表单路径
	private static String loginPath = "admin/login";
	
	private static String commonloginPath = "common/login";
	// 静态资源路径
	private static String resourcesPath = "resources";
	// 无权限页面路径
	private static String deniedPath = "admin/common/error";
	// 登陆提交路径
	private static String submitPath = "admin/submit";
	// 注销
	private static String logoff = "admin/logoff";
	// 主页面
	private static String mainPath = "admin/main";
	// 验证码
	private static String captchaPath = "admin/common/captcha";

	@Autowired
	private AdminService adminSrv;

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

		//request.setCharacterEncoding("UTF-8");
		boolean logdebug = false;
		if (log.isDebugEnabled()) {
			logdebug = true;
		}

		// 上下文路径
		String contextPath = request.getContextPath();
		// 访问路径
		String targetURI = request.getRequestURI().replace(contextPath + "/", "");
		HttpSession session = request.getSession();
		//System.out.println(session.getId());
		// 请求的是静态资源文件(如 .js .css 图片)或者注销，则直接通过
		if (targetURI.contains(resourcesPath) || targetURI.equals(logoff) || targetURI.equals(captchaPath)) {
			return true;
		}

		// 未登录
		if (session.getAttribute(CommonAttributes.Principal) == null) {
			// 登陆路径与登陆表单不拦截
			if (targetURI.equals(submitPath) || targetURI.equals(loginPath)) {
				return true;
			} else {
				// 转到登陆页面
				if (logdebug) {
					log.debug("login is required!");
				}
//				response.sendRedirect(contextPath + "/" + loginPath);
				response.sendRedirect(contextPath + "/" + commonloginPath);
				return false;
			}
			// 已登录
		} else {
			Principal principal = (Principal) request.getSession().getAttribute(CommonAttributes.Principal);
			String username = principal.getUsername();
			
			String role = adminSrv.userRole(username);
			if (role == null || !role.equalsIgnoreCase("admin")) {	//非管理员重新登录
//				session.invalidate();
				return true;
			}
			// 登陆后禁止再访问登陆界面与登陆路径
			if (targetURI.equals(submitPath) || targetURI.equals(loginPath) || targetURI.equals(rootPath)) {
				// 返回主界面
				response.sendRedirect(contextPath + "/" + mainPath);
				return false;
			} else {
				// 访问主界面,或者无权限页面不需要权限
				if (targetURI.equals(mainPath) || targetURI.equals(deniedPath)) {
					return true;
					// 其他请求校验权限
				} else {
					boolean autorized = adminSrv.hasAutohrize(username, targetURI);
					if (autorized) {
						return true;
					} else {
						response.addHeader("code", "403");
						response.setStatus(HttpStatus.SC_FORBIDDEN);
						return false;
					}
				}
			}
		}
	}
}
