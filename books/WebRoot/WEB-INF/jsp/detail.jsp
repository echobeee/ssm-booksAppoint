<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="common/tag.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>图书详情</title>
    <%@include file="common/head.jsp" %>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script type="text/javascript">
	function confirm(){
		var url = "<%=request.getContextPath() %>/appoint?bookId=${book.bookId}";
		$("#url").val(url);
		$("#confirmModal").modal();
	}
	
	function urlSubmit(){
		var url=$.trim($("#url").val());//获取会话中的隐藏属性URL  
		window.location.href=url;    
	}
	
	</script>

  </head>
  
  <body>
    <div class="panel panel-info">
	    <div class="panel-heading">
	        <h3 class="panel-title">
	          	${book.name }<br>
	          	${msg }
	          	
	        </h3>
	    </div>
	    <div class="panel-body">
	       	图书id：${book.bookId } <br>
	       	书名：${book.name } <br>
	       	书目介绍：${book.introd }<br>
	       	库存：${book.number }<br>
	       	<a onclick="confirm()" class="btn btn-info" target="_blank" >预约图书</a>
	    </div>
	</div>
	<!-- 信息删除确认 -->  
	<div class="modal fade" id="confirmModal">  
	  <div class="modal-dialog">  
	    <div class="modal-content message_align">  
	      <div class="modal-header">  
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>  
	        <h4 class="modal-title">提示信息</h4>  
	      </div>  
	      <div class="modal-body">  
	        <p>您确认要预约吗？</p>  
	      </div>  
	      <div class="modal-footer">  
	         <input type="hidden" id="url"/>  
	         <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>  
	         <a  onclick="urlSubmit()" class="btn btn-primary" data-dismiss="modal">确定</a>  
	      </div>  
	    </div><!-- /.modal-content -->  
	  </div><!-- /.modal-dialog -->  
	</div><!-- /.modal -->  
	
	<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
	<script src="http://apps.bdimg.com/libs/jquery/2.0.0/jquery.min.js"></script>
	
	<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
	<script src="http://apps.bdimg.com/libs/bootstrap/3.3.0/js/bootstrap.min.js"></script>
  </body>
</html>
