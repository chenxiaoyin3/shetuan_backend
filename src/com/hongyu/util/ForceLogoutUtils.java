package com.hongyu.util;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import com.hongyu.SessionListener;

public class ForceLogoutUtils {
	 public static void forceUserLogout(String username)  
	    {  
	        if(SessionListener.sessionMap.get( username ) != null)  
	        {  
	            HttpSession session = SessionListener.sessionMap.get( username );  
	  
	            SessionListener.sessionMap.remove( username );  
	  
	            Enumeration e = session.getAttributeNames();  
	  
	            while(e.hasMoreElements())  
	            {  
	                String sessionName = (String) e.nextElement();  
	  
	                session.removeAttribute( sessionName );  
	            }  
	  
	            session.invalidate();  
	        }  
	    }  
}
