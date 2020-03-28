package com.hongyu.filter;

import java.io.IOException;
import java.net.URL;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class CrossDomainFilter extends OncePerRequestFilter {
	  @Override
	  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
			  throws ServletException, IOException {
		String referer = request.getHeader("origin");
		if(referer!=null) {
			if (StringUtils.isNotBlank(referer)) {
//				  URL url = new URL(referer);
//				  if(url==null || url.getProtocol()==null) {
//					  response.addHeader("Access-Control-Allow-Origin", "*");
//					  response.addHeader("Access-Control-Allow-Credentials", "true");
//				  }
//				  else {
//					  String origin = url.getProtocol() + "://" + url.getHost();
//					  if(url.getPort()!=-1){
//						  origin+=":"+url.getPort();
//					  }
					  response.addHeader("Access-Control-Allow-Origin", referer);
					  response.addHeader("Access-Control-Allow-Credentials", "true");
//				  }  
				} 
			else {
				  response.addHeader("Access-Control-Allow-Origin", "*");
//				  response.addHeader("Access-Control-Allow-Credentials", "true");
			}
		}
		else {
			response.addHeader("Access-Control-Allow-Origin", "*");
//			response.addHeader("Access-Control-Allow-Credentials", "true");
		}
//	    response.addHeader("Access-Control-Allow-Credentials", "true");
//		response.addHeader("Access-Control-Allow-Origin", "*");
		  
	    response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
	    response.addHeader("Access-Control-Allow-Headers", "Content-Type");
	    
	    // 增加对Excel跨域的支持
	    response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
	    
	    filterChain.doFilter(request, response);
	  }
}
