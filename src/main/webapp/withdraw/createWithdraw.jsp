<%@ page language="java" import="java.util.*" pageEncoding="gbk"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>��ӭ��������ҳ��</title>

<script type="text/javascript">
	


	function check() {
		var userId = document.getElementById("userId").value;
		var userPass2 = document.getElementById("rechargeMoney").value;
		var regInteger ="/^(0|[1-9][0-9]*)$/";
		var regDouble ="/^[-\+]?\d+(\.\d+)?$/";
		if(regInteger.test(userId)){
			return true;
		}else{
			alert("�������userId����������");
			return false;
		}
		if(regDouble.test(regDouble)){
			return true;
		}else{
			alert("�������Ǯ�������⣬����");
			return false;
		}
		
	}
</script>
  </head>
  
  <body>
     <div align="center"> 
       <h2>��ӭ�������ֽ���</h2>  <br/>
       <form action="/Zis-Pay/withdraw/createWithdrawProtocol" method="post">
                ����������userID: <input type="text"  name="userId" />
     ��������������Ҫ���ֵĽ�� ��<input type="text"  name="withdrawMoney" />
     <input type="submit" >
     </form>
     </div>
  </body>
</html>
