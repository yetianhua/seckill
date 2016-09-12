<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="common/tag.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>秒杀详情页</title>
<%@include file="common/head.jsp"%>
</head>
<body>
	<input id="basePath" type="hidden"
		value="${pageContext.request.contextPath}"></input>
	<!-- 页面显示部分 -->
	<div class="modal fade" id="killPhoneModal" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" data-backdrop="static"
		data-keyboard="false">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h3 class="modal-title">
						<span class="glphyicon glphyicon-phone"></span>秒杀电话:
					</h3>
				</div>
				<div class="modal-body">
					<div class="row">
						<div class="col-xs-8 clo-xs-offset-2">
							<input type="text" name="killPhone" id="killPhoneKey"
								placeholder="填写手机号^o^" class="form-control">
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<div class="alert alert-warning"
						style="display: none; margin-bottom: 0; padding-top: 0; padding-bottom: 0;"
						role="alert" id="killPhoneMessage">
						<strong>Warning!</strong> 手机号错误!
					</div>
					<button type="button" id="killPhoneBtn" class="btn btn-success">
						<span class="glyphicon glyphicon-phone"></span> submit
					</button>
				</div>
			</div>
		</div>
	</div>
	<div class="container">
		<div class="panel panel-default text-center">
			<div class="panel-heading">
				<h1>${seckill.name}</h1>
			</div>
			<div class="panel-body">
				<h2 class="text-danger">
					<span class="glyphicon glyphicon-time"></span> <span
						class="glyphicon" id="seckill-box"></span>
				</h2>
			</div>
		</div>
	</div>

	<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
	<script src="//cdn.bootcss.com/jquery/1.11.3/jquery.min.js"></script>

	<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
	<script src="//cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
	<!-- jquery的cookie插件，用于获取和填写cookies -->
	<script src="//cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.js"></script>
	<!-- jquery的countdown插件，倒计时插件 -->
	<script
		src="//cdn.bootcss.com/jquery.countdown/2.2.0/jquery.countdown.js"></script>

	<!-- 开始编写交互逻辑 -->
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/seckill.js"></script>
	<script type="text/javascript">
		$(function() {
			// 使用EL表达式传入参数
			seckill.detail.init({
				seckillId :${seckill.seckillId},
				startTime : ${seckill.startTime.time},//毫秒
				endTime : ${seckill.endTime.time}
			});
		});
	</script>
</body>
</html>