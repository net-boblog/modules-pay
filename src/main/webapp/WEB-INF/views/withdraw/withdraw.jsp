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
    
    <title>ȷ�����ֽ���</title>

  </head>
  
  <body>
   <div align="center">
    <br/>
    <br/>
    <br/>
    <br/>
    <br/>
        �û�<font color="pink"> ${withdrawProtocol.payerId }</font>��ȷ��Ҫ����<font color="red">${withdrawProtocol.payMoney }</font>Ԫ��<br/>
        <form action="withdraw/withdraw">
          <input type="hidden" name="protocolId" value="${withdrawProtocol.paymentProtocolId }">
          <input type="submit" value="ȷ������">
        </form>
     
   
   </div>
  </body>
</html>
