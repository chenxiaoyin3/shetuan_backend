package com.hongyu.interceptor;

import static com.hongyu.util.Constants.captchaPath;
import static com.hongyu.util.Constants.loginCheckPath;
import static com.hongyu.util.Constants.logoff;
import static com.hongyu.util.Constants.resourcesPath;
import static com.hongyu.util.Constants.submitPath;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.hongyu.CommonAttributes;
import com.hongyu.SessionListener;
import com.hongyu.util.Constants;

public class HyLoginInterceptor implements HandlerInterceptor {
	private transient final Log log = LogFactory.getLog(this.getClass());

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

		boolean logdebug = false;
		if (log.isDebugEnabled()) {
			logdebug = true;
		}
		
		/**
		 * 获取访问的URI
		 */
		String contextPath = request.getContextPath();
		String targetURI = request.getRequestURI().replace(contextPath + "/", "");
		
		HttpSession session = request.getSession();
		
		/**
		 *  请求的是静态资源文件(如 .js .css 图片)或者注销，则直接通过
		 */
		if (targetURI.contains(resourcesPath) || targetURI.equals(logoff) 
				|| targetURI.equals(captchaPath)) {
			return true;
		}
		
		/** 未登录 */
		if (session.getAttribute(CommonAttributes.Principal) == null) {
			/** 判断是否显示登录界面路径 不拦截*/ 
			if (targetURI.equals(Constants.judgeRepeatLoginPath)){
					return true;
			}
			/** 登陆路径不拦截 */
			if (targetURI.equals(submitPath) || targetURI.equals(loginCheckPath)) {
				return true;
			} else{
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				return false;
			}
		} 
//		if(session.getAttribute(CommonAttributes.Principal) != null){
//			/** 登陆路径不允许重复登陆 */
//			if (targetURI.equals(submitPath) || targetURI.equals(loginCheckPath)) {
//				response.setStatus(HttpStatus.SC_CONFLICT);
//				return false;
//			}
//		}
//		else if (targetURI.equals(submitPath)) {
//			response.setStatus(HttpStatus.SC_);
//		}
		
		return true;
	}
}
