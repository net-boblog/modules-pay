<%@ page language="java" import="java.util.*" pageEncoding="gbk"%>
<%@ page isELIgnored="false" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>确定充值成功</title>

  </head>
  
  <body>
   <div align="center">
    <br/>
    <br/>
    <br/>
    请您确定充值成功
    <br/>
    <br/>
    
        <form action="recharge/confirmRecharge">
        请输入充值协议id：<input type="text" name="protocolId" >
            <input type="submit" value="确定充值成功">
        </form>
     
   
   </div>
  </body>
</html>
