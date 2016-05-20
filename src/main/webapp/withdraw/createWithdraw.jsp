<%@ page language="java" import="java.util.*" pageEncoding="gbk"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>欢迎来到提现页面</title>

<script type="text/javascript">
	


	function check() {
		var userId = document.getElementById("userId").value;
		var userPass2 = document.getElementById("rechargeMoney").value;
		var regInteger ="/^(0|[1-9][0-9]*)$/";
		var regDouble ="/^[-\+]?\d+(\.\d+)?$/";
		if(regInteger.test(userId)){
			return true;
		}else{
			alert("您输入的userId必须是整数");
			return false;
		}
		if(regDouble.test(regDouble)){
			return true;
		}else{
			alert("您输入的钱数有问题，请检查");
			return false;
		}
		
	}
</script>
  </head>
  
  <body>
     <div align="center"> 
       <h2>欢迎来到提现界面</h2>  <br/>
       <form action="/Zis-Pay/withdraw/createWithdrawProtocol" method="post">
                请输入您的userID: <input type="text"  name="userId" />
     　　　请输入你要提现的金额 ：<input type="text"  name="withdrawMoney" />
     <input type="submit" >
     </form>
     </div>
  </body>
</html>
