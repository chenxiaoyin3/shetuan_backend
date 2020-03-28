<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
<title>合同下载</title>
</head>
<body>
	<div style="text-align: center;background-color:rgb(54, 138, 247);border-radius: 20px;width: 90%;height: 90%;margin: auto;position: absolute;color: aliceblue;top:0;left:0;right:0;bottom:0">
		<p style="text-align:center"><button style="font-size:60;background-color:rgb(54, 138, 247);border-radius: 10px;border:1px;width: 60%;height: 40%;color:aliceblue ">客户签章成功</button></p>
		<p style="text-align:center">
			<a href="${download_url}" >
				<button style="font-size:40;background-color:#fff;border-radius: 10px;border:1px;width: 60%;height: 20%;color: rgb(54, 138, 247)">下载合同</button>
			</a>
		</p>
		<p style="text-align:center;width: 60%;height: 5%;"></p>
		<p style="text-align:center">
			<a href="${viewpdf_url}" >
				<button style="font-size:40;background-color:rgb(54, 138, 247);border-radius: 10px;border-width:2px;border-style:solid;border-color:#fff;width: 60%;height: 20%;color: aliceblue">查看合同</button>
			</a>
		</p>
	</div>
</body>
</html>