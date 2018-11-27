<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html;charset=utf-8">
		<title>看板 - 出货</title>
		<link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
		<link href="/common.css" rel="stylesheet">
		<script src="/webjars/jquery/jquery.min.js"></script>
		<script src="/webjars/sockjs-client/sockjs.min.js"></script>
		<script src="/echarts.min.js"></script>
		<script src="/common.js"></script>
	</head>
	<body>
		<div class="row">
			<div class="col-md-4">
				<div class="panel panel-primary">
					<div class="panel-heading">
						出货状态
					</div>
					<div class="panel-body">
						<div id="pie-chart" style="height: 300px;"></div>
					</div>
				</div>
			</div>
			<div class="col-md-8">
				<div class="panel panel-primary">
					<div class="panel-heading">
						预约号等待
					</div>
					<div class="panel-body">
						<div id="bar-chart" style="height: 300px;"></div>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="panel panel-primary">
					<div class="panel-heading">
						台车信息
					</div>
					<div class="panel-body">
						<table id="data-table" class="table table-striped">
							<thead>
								<tr>
									<th>台车号</th>
									<th>厂别</th>
									<th>线别</th>
									<th>料盒数量</th>
									<th>预约流水号</th>
									<th>状态</th>
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

var STATUS_MAP = {
		APPOINTMENT: 0,
		WAITING: 1,
		UNFINISHED: 2},
	STATUS_TRANS = {
		APPOINTMENT: '预约',
		WAITING: '等待',
		UNFINISHED: '未完成'};

var piechart = PieChart('pie-chart', '单数', STATUS_TRANS);
var barchart = SingleBarChart('bar-chart', '等待时间', '{value}');
var datatable = DataTable('data-table', 5000, [
	function(row) { return row.trolleyId; },
	function(row) { return row.factory || '-'; },
	function(row) { return row.line || '-'; },
	function(row) { return row.boxQty; },
	function(row) { return row.appointmentKey || '-'; },
	function(row) { return STATUS_TRANS[row.status] || '-'; }
]);

function convertStatus(value) {
	if (value.status in STATUS_MAP)
		return STATUS_MAP[value.status];
	return 3;
}
function updateTableData(values) {
	if (values.sort) {
		values.sort(function (a, b) {
			var sa = convertStatus(a), sb = convertStatus(b);
			if (sa < sb) return -1;
			if (sa > sb) return 1;
			if (a.appointmentKey < b.appointmentKey) return -1;
			if (a.appointmentKey > b.appointmentKey) return 1;
			if (a.factory < b.factory) return -1;
			if (a.factory > b.factory) return 1;
			if (a.line < b.line) return -1;
			if (a.line > b.line) return 1;
			return 0;
		});
		datatable.update(values);
	}
}

var ws = null;
function connect() {
	ws = new SockJS('/sockjs/shipment');
	ws.onmessage = function(evt) {
		var arr = evt.data.split(':');
		if (arr.length) {
			if (arr[0] == 'pie') {
				piechart.update(deserializeMessage(arr[1], parseInt));
			} else if (arr[0] == 'bar') {
				var key = arr[2] + '(' + arr[4] + ')';
				if (arr[1] == 'add') {
					var value = new Date().getTime() - new Date(arr[3]).getTime();
					barchart.update(key, value);
				} else if (arr[1] == 'remove') {
					barchart.remove(key);
				}
			}
		}
	};
	ws.onopen = function(evt) {
		$('#error-message').hide();
		$.get('/shipment/table.json', updateTableData).done(datatable.render);
		$.get('/shipment/appointments.json', function(data) {
			barchart.load(data.map(function(item) {
				return {
					key: item.key + '(' + item.factory + '-' + item.line + ')',
					value: new Date().getTime() - new Date(item.start).getTime()
				};
			}));
		});
		$.get('/shipment/status.json', piechart.update);
	};
	ws.onclose = function(evt) {
		var e = $('#error-message'), w = $(window);
		e.css({
			top: (w.height() - e.height()) / 2,
			left: (w.width() - e.width()) / 2
		}).show();
		datatable.clear();
		barchart.clear();
		piechart.clear();
		setTimeout(connect, 1000);
	};
}
connect();
setInterval(function() {
	$.get('/shipment/table.json', updateTableData);
}, 5000);
$(window).on('resize', function() {
	var w = $(window),
		estHeight = (w.height() - 200) / 3 - 80,
		qBarChart = $('#bar-chart'),
		qPieChart = $('#pie-chart');
	if (estHeight < 300) estHeight = 300;
	qPieChart.height(estHeight);
	qBarChart.height(estHeight);
	piechart.rebind('pie-chart');
	piechart.render();
	barchart.rebind('bar-chart');
	barchart.render();
});
	</script>
</html>
