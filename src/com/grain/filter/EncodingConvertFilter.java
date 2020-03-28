package com.grain.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter - 编码格式转换
 * 
 */
public class EncodingConvertFilter extends OncePerRequestFilter {

	/** 原编码格式 */
	private String fromEncoding = "ISO-8859-1";

	/** 目标编码格式 */
	private String toEncoding = "UTF-8";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
/*		if (request.getMethod().equalsIgnoreCase("GET")) {
			for (Iterator<String[]> iterator = request.getParameterMap().values().iterator(); iterator.hasNext();) {
				String[] parames = iterator.next();
				for (int i = 0; i < parames.length; i++) {
					try {
						parames[i] = new String(parames[i].getBytes(fromEncoding), toEncoding);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
		filterChain.doFilter(request, response);*/
	     HttpServletRequest requ = (HttpServletRequest) request;  
	        HttpServletResponse resp = (HttpServletResponse) response;  
	          
	        //使用包装类  
	        MyHttpServletRequest myrequest = new MyHttpServletRequest(requ);  
	          
	        filterChain.doFilter(myrequest, resp);  
	}

	/**
	 * 获取原编码格式
	 * 
	 * @return 原编码格式
	 */
	public String getFromEncoding() {
		return fromEncoding;
	}

	/**
	 * 设置原编码格式
	 * 
	 * @param fromEncoding
	 *            原编码格式
	 */
	public void setFromEncoding(String fromEncoding) {
		this.fromEncoding = fromEncoding;
	}

	/**
	 * 获取目标编码格式
	 * 
	 * @return 目标编码格式
	 */
	public String getToEncoding() {
		return toEncoding;
	}

	/**
	 * 设置目标编码格式
	 * 
	 * @param toEncoding
	 *            目标编码格式
	 */
	public void setToEncoding(String toEncoding) {
		this.toEncoding = toEncoding;
	}

	private class MyHttpServletRequest extends HttpServletRequestWrapper {  
        private HttpServletRequest request;  
          
        public MyHttpServletRequest(HttpServletRequest request) {  
            super(request);  
            this.request = request;  
        }  
  
        //求某个  
        @Override  
        public String getParameter(String name) {  
            Map<String, String[]> map = getParameterMap();  
            if(map != null){  
                String[] values = map.get(name);  
                if(values != null){  
                    return values[0];  
                }  
            }  
            return null;  
        }  
          
  
        @Override  
        public String[] getParameterValues(String name) {  
            Map<String, String[]> map = getParameterMap();  
            if(map != null){  
                String[] values = map.get(name);  
                if(values != null){  
                    return values;  
                }  
            }  
            return null;  
        }  
  
          
        @Override  
        public Map<String, String[]> getParameterMap() {  
            if("post".equalsIgnoreCase(request.getMethod())){  
                //post方式提交  
                try {  
                    request.setCharacterEncoding(toEncoding);  
                } catch (UnsupportedEncodingException e) {  
                    e.printStackTrace();  
                }  
                return request.getParameterMap();  
            }else {  
                //get方式提交  
                Map<String, String[]> paramMap = request.getParameterMap();  
//                    for(Map.Entry<String, String[]> me : paramMap.entrySet()){  
//                        String[] values = me.getValue();  
//                        for(int i = 0; i < values.length; i++){  
//                            try {  
//                                values[i] = new String(values[i].getBytes(fromEncoding), toEncoding);  
//                            } catch (UnsupportedEncodingException e) {  
//                                e.printStackTrace();  
//                            }  
//                        }  
//                    }  
                return paramMap;  
            }  
        }  
	}
}