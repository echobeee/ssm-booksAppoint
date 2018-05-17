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
    
  <script type="text/javascript">
  function mytime(){
      var a = new Date();
      var b = a.toLocaleTimeString();
      var c = a.toLocaleDateString();
      document.getElementById("appoint-box").innerHTML = c+"&nbsp"+b;
      }
  setInterval(function() {mytime()},1000);
  </script>
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
            <input type="submit" class="btn btn-info" value="search"/>
        </form>
    </div>	

	<div class="navbar-right">
		<ul class="nav navbar-nav navbar-right">
			<li><a href="<%=request.getContextPath() %>/myAppoint">我的预约</a></li>
           <li class="navbar-text">您好！${stu.studentId }</li>
           <c:choose>
           <c:when test="${stu eq null }">
           		<li><a href="<%=request.getContextPath() %>/inlogin">请登录</a></li>
           </c:when>
           <c:otherwise>
           		<li><a href="<%=request.getContextPath() %>/logout">注销</a></li>
           </c:otherwise>
           </c:choose>
           <li class="navbar-text" id="appoint-box" style="color: red"></li>
        </ul>
	</div>

	</div>
   </nav>
  	<div class="panel panel-primary">
	    <div class="panel-heading text-center">
	        <h3 class="panel-title">图书列表</h3>
	    </div>
    <div class="panel-body">
    	<table class="table table-hover">
	        <tr>
	        	<th>图书id</th>
	        	<th>书名</th>
	        	<th>库存</th>
	        	<th>详细</th>    
	        	<th>预约图书</th>
	        </tr>
	        <c:forEach items="${bookList.list }" var="book">
	        <tr>
		        <td>${book.bookId }</td>
		        <td>${book.name }</td>
		        <td>${book.number }</td>
		        <td><a href="<%=request.getContextPath() %>/findBook?bookId=${book.bookId}" class="btn btn-info" target="_blank" >详细</a></td>
	        	<td><button type="button" class="btn btn-info appoint" value="${book.bookId }">
	        	预约
	        	</button>
	        	</td>
	        </tr>
   			</c:forEach>
   		</table>
   		<!-- 信息删除确认 -->  
		<div class="modal fade" id="confirmModal">  
		  <div class="modal-dialog">  
		    <div class="modal-content message_align">  
		      <div class="modal-header">  
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>  
		        <h4 class="modal-title">请先登录哦</h4>  
		      </div>  
		      <div class="modal-body">  
		        <div class="row">
                    <div class="col-xs-6 col-xs-offset-2">
                        <input type="text" name="id" id="studentIdKey"
                               placeholder="学号" class="form-control" >
                    </div>
                    <div class="col-xs-6 col-xs-offset-2">
                        <input type="password" name="password" id="passwordKey"
                               placeholder="密码" class="form-control">
                    </div>
                </div>
		      </div>  
		      <div class="modal-footer">  
		         <input type="hidden" id="url"/>  
		         <span id="studentMessage" class="glyphicon"> </span> 
		         <button type="button" id="studentBtn" class="btn btn-success">
                    <span class="glyphicon glyphicon-student"></span>
                    	登录
                </button>  
		      </div>  
		    </div><!-- /.modal-content -->  
		  </div><!-- /.modal-dialog -->  
		</div><!-- /.modal -->  
   			<%-- 分页
   				1. 总页数小于10 则直接显示1-10
   				2.否则 常规状态
   					判断头溢出
   					判断尾溢出
   			 --%>
   			 <div class="text-center">
   			 <a href="<%=request.getContextPath() %>/findAllBooks?pageSize=7&page=1">首页</a>
   			 <c:if test="${bookList.hasPreviousPage }">
   			 <a href="<%=request.getContextPath() %>/findAllBooks?pageSize=7&page=${bookList.prePage}">上一页</a>
   			 </c:if>
   			 <c:choose>
   			 <%-- 若总页数小于10，全部显示 --%>
   			 	<c:when test="${bookList.pages < 10 }">
   			 		<c:set var="begin" value="1"></c:set>
   			 		<c:set var="end" value="${bookList.pages }"/>
   			 	</c:when>
   			 <%-- 否则，常规显示 --%>
   			 <c:otherwise>
   			 	<c:set var="begin" value="${bookList.pageNum-4 }"/>
   			 	<c:set var="end" value="${bookList.pageNum+5 }"/>
   			 	<%-- 头部溢出 --%>
   			 	<c:if test="${begin<1 }">
   			 		<c:set var="begin" value="1"/>
   			 		<c:set var="end" value="10"/>
   			 	</c:if>
   			 	<%-- 尾部溢出 --%>
   			 	<c:if test="${end>bookList.pages }">
   			 		<c:set var="begin" value="${bookList.pages-9 }"/>
   			 		<c:set var="end" value="${bookList.pages }"/>
   			 	</c:if>
   			 </c:otherwise>
   			 </c:choose>
   			 <c:forEach var="i" begin="${begin }" end="${end }" >
   			 	<c:choose>
   			 		<c:when test="${i eq bookList.pageNum }">
   			 		[${i }]
   			 		</c:when>
   			 		<c:otherwise>
   			 		<a href="<%=request.getContextPath() %>/findAllBooks?pageSize=7&page=${i}">[${i }]</a>
   			 		</c:otherwise>
   			 	</c:choose>
   			 </c:forEach>
   			 <c:if test="${bookList.hasNextPage }">
   			 <a href="<%=request.getContextPath() %>/findAllBooks?pageSize=7&page=${bookList.nextPage}">下一页</a>
   			 </c:if>
   			 <a href="<%=request.getContextPath() %>/findAllBooks?pageSize=7&page=${bookList.pages}">尾页</a>
   			 
   			 </div>
   		</div>
</div>
  
  
	<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
	<script src="http://apps.bdimg.com/libs/jquery/2.0.0/jquery.min.js"></script>
	
	<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
	<script src="http://apps.bdimg.com/libs/bootstrap/3.3.0/js/bootstrap.min.js"></script>
 	<%--jQuery Cookie操作插件--%>
	<script src="http://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
 	<script src="js/bookappoint.js"></script>
  </body>
  
</html>
			