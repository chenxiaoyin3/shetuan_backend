<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
<title>返回商城</title>
</head>
<body>
	<div style="text-align: center;background-color:rgb(54, 138, 247);border-radius: 20px;width: 90%;height: 90%;margin: auto;position: absolute;color: aliceblue;top:0;left:0;right:0;bottom:0">
		<p style="text-align:center"><button style="font-size:60;background-color:rgb(54, 138, 247);border-radius: 10px;border:1px;width: 60%;height: 40%;color:aliceblue ">支付成功</button></p>
		<p style="text-align:center">
			<a href="${return_url}" >
				<button style="font-size:40;background-color:#fff;border-radius: 10px;border:1px;width: 60%;height: 20%;color: rgb(54, 138, 247)">返回商城</button>
			</a>
		</p>
	</div>
</body>
</html>