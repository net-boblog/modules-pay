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
    
    <title>ȷ����ֵ�ɹ�</title>

  </head>
  
  <body>
   <div align="center">
    <br/>
    <br/>
    <br/>
    ����ȷ����ֵ�ɹ�
    <br/>
    <br/>
    
        <form action="recharge/confirmRecharge">
        �������ֵЭ��id��<input type="text" name="protocolId" >
            <input type="submit" value="ȷ����ֵ�ɹ�">
        </form>
     
   
   </div>
  </body>
</html>
