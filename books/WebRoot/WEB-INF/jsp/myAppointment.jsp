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
    
    <title>图书列表</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
    <title>图书列表</title>
    <%@include file="common/head.jsp" %>

  </head>
  
  <body>
  <nav class="navbar navbar-default" role="navigation">
	<div class="container-fluid">
	<div class="navbar-header">
		<a class="navbar-brand" href="<%=request.getContextPath() %>/findAllBooks">BookAppointment</a>
	</div>

	<div>
        <form action="<%=request.getContextPath() %>/findBooks" class="navbar-form navbar-left" role="search">
            <div class="form-group">
                <input type="text" class="form-control" style="width: 500px" name="keyword" placeholder="搜索书目">
            </div>
            <input type="submit" class="btn btn-default" value="search"/>
        </form>
    </div>	

	<div class="navbar-right">
		<ul class="nav navbar-nav navbar-right">
			<li><a href="<%=request.getContextPath() %>/myAppoint">我的预约</a></li>
           <li class="navbar-text">您好！${stu.studentId }</li>
           <li><a href="<%=request.getContextPath() %>/logout">注销</a></li>
        </ul>
	</div>

	</div>
   </nav>
  	<div class="panel panel-primary">
	    <div class="panel-heading text-center">
	        <h3 class="panel-title">我的预约</h3>
	    </div>
    <div class="panel-body">
    	<table class="table table-hover">
	        <tr>
	        	<th>图书id</th>
	        	<th>预约id</th>
	        	<th>时间</th>
	        	<th>详细</th>    
	        	<th>预约图书</th>
	        </tr>
	        <c:forEach items="${appointList }" var="appoint">
	        <tr>
		        <td>${appoint.bookId }</td>
		        <td>${appoint.studentId }</td>
		        <td><fmt:formatDate value="${appoint.appointTime}" pattern="yyyy-MM-dd HH:mm"/></td>
		        <td><a href="<%=request.getContextPath() %>/findBook?bookId=${appoint.bookId}" class="btn btn-info" target="_blank" >详细</a></td>
	        	<td><a href="<%=request.getContextPath() %>/appoint?bookId=${appoint.bookId}" class="btn btn-info" target="_blank" >预约</a></td>
	        </tr>
   			</c:forEach>
   		</table>
    </div>
    
</div>
  
  
	<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
	<script src="http://apps.bdimg.com/libs/jquery/2.0.0/jquery.min.js"></script>
	
	<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
	<script src="http://apps.bdimg.com/libs/bootstrap/3.3.0/js/bootstrap.min.js"></script>
  </body>
  
</html>
			