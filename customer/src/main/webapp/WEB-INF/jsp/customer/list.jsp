<%--
  Created by IntelliJ IDEA.
  User: zsm
  Date: 2019/9/8
  Time: 20:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>List</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link href="${pageContext.request.contextPath}/static/css/application.css" rel="stylesheet"> 
    <script src="${pageContext.request.contextPath}/static/js/application.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/jquery_2.2.3.min.js"></script>
</head>
<body>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
   <%--  <%@taglib prefix="myTag" tagdir="/WEB-INF/static/tags" %> --%>
    <table width="100%" border="1" cellpadding="0" cellspacing="1" class="tableLine">
        <tr>
            <td colspan="5" align="center" class="tableLineBg">
                用户列表
            </td>
        </tr>
        <tr>
            <td>客户编号</td>
            <td>客户密码</td>
            <td>显示名称</td>
            <td>真实姓名</td>
            <td>操作</td>
        </tr>
        <c:forEach var="m" items="${page.result}">
            <tr>
                <td>${m.customerId}</td>
                <td>${m.pwd}</td>
                <td>${m.showName}</td>
                <td>${m.trueName}</td>
                <td>
	                <a href="${pageContext.request.contextPath}/customer/toUpdate/${m.uuid}">修改</a>
	                <a href="${pageContext.request.contextPath}/customer/toDelete/${m.uuid}">删除</a>
                </td>
                
            </tr>
        </c:forEach>
    </table>
</body>
</html>
