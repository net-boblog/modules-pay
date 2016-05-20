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
    
    <title>确定提现界面</title>

  </head>
  
  <body>
   <div align="center">
    <br/>
    <br/>
    <br/>
    <br/>
    <br/>
        用户<font color="pink"> ${withdrawProtocol.payerId }</font>您确定要提现<font color="red">${withdrawProtocol.payMoney }</font>元吗？<br/>
        <form action="withdraw/withdraw">
          <input type="hidden" name="protocolId" value="${withdrawProtocol.paymentProtocolId }">
          <input type="submit" value="确定提现">
        </form>
     
   
   </div>
  </body>
</html>
