package com.hongyu;

import java.util.HashMap;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {

	public static HashMap<String,HttpSession> sessionMap = new HashMap<>();

	@Override
	public void sessionCreated(HttpSessionEvent httpSessionEvent) {
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
		// System.out.println("进入了sessionListener的destroy方法");
		HttpSession session = httpSessionEvent.getSession();
		delSession(session);
	}
	
	public static synchronized void delSession(HttpSession session) {
		if(session != null) {
			if(session.getAttribute(CommonAttributes.Principal) != null) {
				String admin = (String) session.getAttribute(CommonAttributes.Principal);
				SessionListener.sessionMap.remove(admin);
			}
		}
	}
}
