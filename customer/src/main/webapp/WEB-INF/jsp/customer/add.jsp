<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Add</title>
     <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link href="${pageContext.request.contextPath}/static/css/application.css" rel="stylesheet"> 
</head>
<body>
     <form action="${pageContext.request.contextPath}/customer/add" method="post">
        <table width="100%" border="1" cellpadding="0" cellspacing="1" class="tableLine">
            <tr>
                <td colspan="4" align="center" class="tableLineBg">
                    新增用户
                </td>
            </tr>
            <tr>
                <td>客户编号</td>
                <td><input type="text" name="customerId" class="input"></td>
                <td>客户密码</td>
                <td><input type="text" name="pwd" class="input"></td>
            </tr>
            <tr>
                <td>显示名称</td>
                <td><input type="text" name="showName" class="input"></td>
                <td>真实姓名</td>
                <td><input type="text" name="trueName" class="input"></td>
            </tr>
            <tr>
                <td colspan="4" align="center">
                    <input type="submit" value="新增" class="button">
                </td>
            </tr>
        </table>
    </form> 
</body>
</html>
