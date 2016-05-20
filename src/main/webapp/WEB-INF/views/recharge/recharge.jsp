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
    
    <title>确定充值界面</title>

  </head>
  
  <body>
   <div align="center">
    <br/>
    <br/>
    <br/>
    <br/>
    <br/>
        用户<font color="pink"> ${rechargeProtocol.receiverId }</font>您确定要充值<font color="red">${rechargeProtocol.payMoney }</font>元吗？<br/>
    <!--     <form action="recharge/recharge">
        <input type="hidden" name="protocolId" value="${rechargeProtocol.paymentProtocolId }">
          支付方式：  <input type="text" name="channel">
            <input type="submit" value="确定充值">
        </form>
      --> 
         <form action="alipayapi.jsp">
              <input type="hidden" name="WIDout_trade_no" value="rechargeProtocol${rechargeProtocol.paymentProtocolId }">
              <input type="hidden" name="WIDsubject" value=" user${rechargeProtocol.receiverId }recharge${rechargeProtocol.payMoney}">
              <input type="hidden" name="WIDtotal_fee" value="${rechargeProtocol.payMoney}">
              <input type="hidden" name="WIDbody" value=" user${rechargeProtocol.receiverId }recharge${rechargeProtocol.payMoney}">
              
             <input type="submit" value="确定充值">
         </form>
   </div>
  </body>
</html>
