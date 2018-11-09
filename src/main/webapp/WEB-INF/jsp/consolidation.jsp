<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html;charset=utf-8">
		<title>看板 - 集货</title>
		<link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
		<script src="/webjars/jquery/jquery.min.js"></script>
		<script src="/webjars/sockjs-client/sockjs.min.js"></script>
		<script src="/webjars/stomp-websocket/stomp.min.js"></script>
		<script src="/echarts.min.js"></script>
		<style>
			body {
				background-color: #061325;
				padding: 100px;
			}

			.panel-primary>.panel-heading {
				color: #70cac7;
				background-color: #06131b;
			}

			.panel-primary>.panel-body {
				background-color: #001531;
			}
		</style>
	</head>
	<body>
		<div class="row">
			<div class="col-md-4">
				<div class="panel panel-primary">
					<div class="panel-heading">
						集货量
					</div>
					<div class="panel-body">
						<div id="pie-chart" style="height: 300px;"></div>
					</div>
				</div>
			</div>
			<div class="col-md-8">
				<div class="panel panel-primary">
					<div class="panel-heading">
						集货量变化
					</div>
					<div class="panel-body">
						<div id="line-chart" style="height: 300px;"></div>
					</div>
				</div>
			</div>
		</div>
		<div style="position: fixed; top: 100px; display: none; text-align: center;" id="error-message" class="alert alert-danger">
			服务器连接错误
		</div>
	</body>
</html>
