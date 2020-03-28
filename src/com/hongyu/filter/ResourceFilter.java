package com.hongyu.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by xyy on 2019/3/20.
 *
 * @author xyy
 */
public class ResourceFilter extends OncePerRequestFilter {
    /** 将Access-Control-Allow-Headers的Content-Type修改为X-Requested-With*/
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Headers", "X-Requested-With");
        filterChain.doFilter(request, response);
    }
}
