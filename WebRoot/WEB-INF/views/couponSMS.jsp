<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>      
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>    
<%      
    String path = request.getContextPath();      
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";      
 %>      
 <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
 
 <!-- 大客户购买电子券使在网页上查看 -->
 
 
<html>      
    <head>      
      <title>【虹宇国际旅行社】电子券激活码</title>      
    </head>      
          
    <body>      
        <table border="0" cellpadding="10" cellspacing="0" >    
            <tr>    
                <th>电子券编号</th>    
                <th>激活码</th>   
                <th>激活状态</th> 
            </tr>    
                
            <c:forEach items="${couponList}" var="coupon">    
                <tr>    
                    <td>${coupon.code}</td>    
                    <td>${coupon.activateCode}</td>  
                    <td>${coupon.isActivied}</td>     
                </tr>    
            </c:forEach>    
        </table>    
    </body>      
</html>  