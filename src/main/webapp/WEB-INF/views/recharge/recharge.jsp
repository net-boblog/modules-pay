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
    
    <title>ȷ����ֵ����</title>

  </head>
  
  <body>
   <div align="center">
    <br/>
    <br/>
    <br/>
    <br/>
    <br/>
        �û�<font color="pink"> ${rechargeProtocol.receiverId }</font>��ȷ��Ҫ��ֵ<font color="red">${rechargeProtocol.payMoney }</font>Ԫ��<br/>
    <!--     <form action="recharge/recharge">
        <input type="hidden" name="protocolId" value="${rechargeProtocol.paymentProtocolId }">
          ֧����ʽ��  <input type="text" name="channel">
            <input type="submit" value="ȷ����ֵ">
        </form>
      --> 
         <form action="alipayapi.jsp">
              <input type="hidden" name="WIDout_trade_no" value="rechargeProtocol${rechargeProtocol.paymentProtocolId }">
              <input type="hidden" name="WIDsubject" value=" user${rechargeProtocol.receiverId }recharge${rechargeProtocol.payMoney}">
              <input type="hidden" name="WIDtotal_fee" value="${rechargeProtocol.payMoney}">
              <input type="hidden" name="WIDbody" value=" user${rechargeProtocol.receiverId }recharge${rechargeProtocol.payMoney}">
              
             <input type="submit" value="ȷ����ֵ">
         </form>
   </div>
  </body>
</html>
