// 该js功能：
// 预约前需要登录
// 要引入jquery cookie插件
window.onload = function(){
    	$('.appoint').click(function(){
    		console.log($(this).attr("value"));
    		confirm($(this).attr("value"));
    	});
    	$('#studentBtn').click(urlSubmit);
    	
    	function confirm(bookId){
    	    	var url = "logon";
    	    	$("#url").val(url);
    	    	var id =$.cookie("stuid");
    	    	console.log(id);
    	    	if(id == undefined){ //只有session中没有该属性才要登录框
    	    		$("#confirmModal").modal();
    	    	} else { //否则直接预约
    	    		url = "appoint?bookId="+bookId;
    	    		window.location.href=url;    
    	    	}
    	    }

    	   function urlSubmit(){
    	    	var url=$.trim($("#url").val());//获取会话中的隐藏属性URL 
    	    	var params = {};// 获取帐号密码
    	    	params.id = $('#studentIdKey').val();
    	    	console.log(params.id);
    	    	if(params.id == ''){
    	    		params.id = 1;
    	    	}
    	    	params.password = $('#passwordKey').val();
    	    	if(params.password == ''){
    	    		params.password = 1;
    	    	}
    	    	$.ajax({
    	    		type:"post",
    	    		url:url,
    	    		data:params,
    	    		datatype:'josn', 
    	    	//	async:false,     
    	    		success:function(data){
    	    			if(data == "SUCCESS"){
    	    				window.location.reload();//页面刷新
    	    				alert("登录成功！");
    	    			}else{
    	    				$('#studentMessage').hide().html('<label class="label label-danger">帐号或密码错误!</label>').show(300);
    	    			}
    	    		}
    	    	});    
    	    }
    	   
    };