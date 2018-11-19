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
		<script src="/echarts.min.js"></script>
		<script src="/common.js"></script>
		<style>
			html, body {
				height: 100%;
			}

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

			.table {
				color: #a5c8e6;
			}
			.table > thead > tr > th {
				border-bottom: 2px solid #2f6899;
			}
			.table > tbody > tr > td {
				border-bottom: 1px solid #2f6899;
			}
			.table-striped > tbody > tr:nth-of-type(2n+1) {
				background-color: #032045;
			}

			#error-message {
				position: fixed;
				top: 100px;
				display: none;
				text-align: center;
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
		<div class="row">
			<div class="col-md-12">
				<div class="panel panel-primary">
					<div class="panel-heading">
						集货信息
					</div>
					<div class="panel-body">
						<table id="data-table" class="table table-striped">
							<thead>
								<tr>
									<th>台车号</th>
									<th>是否拼单</th>
									<th>单号</th>
									<th>货主</th>
									<th>收货人</th>
									<th>厂别</th>
									<th>线别</th>
									<th>剩余时间</th>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		<div id="error-message" class="alert alert-danger">
			服务器连接错误
		</div>
	</body>
	<script type="text/javascript">

var piechart = PieChart('pie-chart', '单数', {
	PICKED: '已拣货',
	SHIPPED: '已发货',
	OTHER: '其它'
});
var linechart = LineChart('line-chart', '{value}', {
	PICKED: '已拣货',
	SHIPPED: '已发货',
	OTHER: '其它'
});
var datatable = DataTable('data-table', 5000, [
	function(row) { return row.trolleyId; },
	function(row) { return row.toCombine ? '是' : '否'; },
	function(row) { return row.orderKey; },
	function(row) { return formatNameAndKey(row.storerName, row.storer); },
	function(row) { return formatNameAndKey(row.consigneeName, row.consignee); },
	function(row) { return row.factory || '-'; },
	function(row) { return row.line || '-'; },
	function(row) { return formatDuration2(calcRemainingTime(row.shipDate)); }
]);

var ws = null;
function connect() {
	ws = new SockJS('/sockjs');
	ws.onmessage = function(evt) {
		var arr = evt.data.split(':');
		if (arr.length) {
			if (arr[0] == 'pie') {
				piechart.update(deserializeMessage(arr[1], parseInt));
			} else if (arr[0] == 'line') {
				var t = parseInt(arr[1]);
				linechart.update(t, deserializeMessage(arr[2], parseInt));
				console.log(arr);
			} else if (arr[0] == 'init') {
				$.get('/consolidation/history.json', linechart.load);
			}
		}
	};
	ws.onopen = function(evt) {
		$('#error-message').hide();
		$.get('/consolidation/table.json', datatable.update).done(datatable.render);
		$.get('/consolidation/history.json', linechart.load);
		$.get('/consolidation/status.json', piechart.update);
	};
	ws.onclose = function(evt) {
		var e = $('#error-message'), w = $(window);
		e.css({
			top: (w.height() - e.height()) / 2,
			left: (w.width() - e.width()) / 2
		}).show();
		datatable.clear();
		linechart.clear();
		piechart.clear();
		setTimeout(connect, 1000);
	};
}
connect();
setInterval(function() {
	$.get('/consolidation/table.json', datatable.update);
}, 5000);

	</script>
</html>
