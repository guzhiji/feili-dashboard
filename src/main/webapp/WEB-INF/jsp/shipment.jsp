<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html;charset=utf-8">
		<title>看板 - 出货</title>
		<link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
		<link href="/build/styles.min.css?1547705916" rel="stylesheet">
		<script src="/webjars/jquery/jquery.min.js"></script>
		<script src="/webjars/sockjs-client/sockjs.min.js"></script>
		<script src="/echarts.min.js"></script>
		<script src="/build/common.js?1547630069"></script>
		<script src="/build/theme.js?1547630069"></script>
	</head>
	<body>
		<h1>出货看板</h1>
		<div class="row">
			<div class="col-md-4">
				<div class="panel panel-success">
					<div class="panel-heading">
						出货订单状态
					</div>
					<div class="panel-body">
						<div id="pie-chart" style="height: 300px;"></div>
					</div>
				</div>
				<div class="panel panel-success">
					<div class="panel-heading">
						预约计时
					</div>
					<div class="panel-body">
						<div id="bar-chart" style="height: 300px;"></div>
					</div>
				</div>
			</div>
			<div class="col-md-8">
				<div class="panel panel-success">
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

function estimateChartSizes() {
	var w = $(window),
		pieHeight = (w.height() - 200) / 3 - 80,
		qPieChart = $('#pie-chart'),
		qBarChart = $('#bar-chart'),
		barOffset = qBarChart.offset(),
		barHeight = w.height() - barOffset.top - 128;
	if (pieHeight < 300) pieHeight = 300;
	if (barHeight < 300) barHeight = 300;
	qPieChart.height(pieHeight);
	qBarChart.height(barHeight);
}
estimateChartSizes();
var piechart = PieChart('pie-chart', '单数', STATUS_TRANS);
var barchart = SingleBarChart('bar-chart', '等待时间', true, formatDuration);
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
			if (a.trolleyId < b.trolleyId) return -1;
			if (a.trolleyId > b.trolleyId) return 1;
			return 0;
		});
		datatable.update(values);
	}
}

var chartFontSize = estimateChartFontSize();
piechart.updateFontSize(chartFontSize);
barchart.updateFontSize(chartFontSize);

var barData = {};
var connected = false;
var ws = null;
function connect() {
	ws = new SockJS('/sockjs/shipment');
	ws.onmessage = function(evt) {
		var arr = evt.data.split(':');
		if (arr.length) {
			if (arr[0] == 'pie') {
				piechart.update(deserializeMessage(arr[1], parseInt));
			} else if (arr[0] == 'bar') {
				var key = arr[2];
				if (arr[1] == 'add') {
					var value = parseInt(arr[3]),
						label = key + (arr[4] ? '\n(' + arr[4] + ')' : ''),
						diff = uptoNow(value);
					barData[key] = value;
					barchart.update(key, label, diff);
				} else if (arr[1] == 'remove') {
					barchart.remove(key);
					if (key in barData) delete barData[key];
				}
			} else if (arr[0] == 'basetime') {
				setBaseTime(arr[1]);
			}
		}
	};
	ws.onopen = function(evt) {
		connected = true;
		$('#error-message').fadeOut();
		$.get('/shipment/table.json', updateTableData).done(datatable.render);
		$.get('/shipment/appointments.json', function(data) {
			barData = {};
			barchart.load(data.map(function(item) {
				var label = item.key;
				if (item.factory || item.line)
					label += '\n(' + (item.factory || '') + '-' + (item.line || '') + ')';
				barData[item.key]  = item.start;
				return {key: item.key, label: label, value: uptoNow(item.start)};
			}));
		});
		$.get('/shipment/status.json', piechart.update);
	};
	ws.onclose = function(evt) {
		var e = $('#error-message'), w = $(window);
		e.css({
			top: (w.height() - e.height()) / 2,
			left: (w.width() - e.width()) / 2
		}).fadeIn();
		datatable.clear();
		barchart.clear();
		piechart.clear();
		connected = false;
		setTimeout(connect, 1000);
	};
}
connect();
setInterval(function() {
	if (connected) {
		for (var key in barData)
			barchart.update(key, null, uptoNow(barData[key]));
	}
}, 1000);
setInterval(function() {
	if (connected)
		$.get('/shipment/table.json', updateTableData);
}, 5000);
$(window).on('resize', function() {
	estimateChartSizes();

	piechart.rebind('pie-chart');
	barchart.rebind('bar-chart');

	var chartFontSize = estimateChartFontSize();
	piechart.updateFontSize(chartFontSize);
	barchart.updateFontSize(chartFontSize);

	piechart.render();
	barchart.render();
});

theme.register('transpblue', '蓝色透明风格', function() {
	$('body')
		.removeClass('transpblue')
		.removeClass('transporange')
		.addClass('transpblue');
	$('.panel')
		.removeClass('panel-success')
		.removeClass('panel-primary')
		.removeClass('panel-transpblue')
		.removeClass('panel-transporange')
		.addClass('panel-transpblue');
	piechart.updateFontColor(COLOR_TEXT);
	barchart.updateFontColor(COLOR_TEXT);
});
theme.register('transporange', '橘色透明风格', function() {
	$('body')
		.removeClass('transpblue')
		.removeClass('transporange')
		.addClass('transporange');
	$('.panel')
		.removeClass('panel-success')
		.removeClass('panel-primary')
		.removeClass('panel-transpblue')
		.removeClass('panel-transporange')
		.addClass('panel-transporange');
	piechart.updateFontColor('#612d07');
	barchart.updateFontColor('#612d07');
});
theme.register('blue', '蓝色风格', function() {
	$('body')
		.removeClass('transpblue')
		.removeClass('transporange');
	$('.panel')
		.removeClass('panel-success')
		.removeClass('panel-primary')
		.removeClass('panel-transpblue')
		.removeClass('panel-transporange')
		.addClass('panel-primary');
	piechart.updateFontColor(COLOR_TEXT);
	barchart.updateFontColor(COLOR_TEXT);
});
theme.register('green', '绿色风格', function() {
	$('body')
		.removeClass('transpblue')
		.removeClass('transporange');
	$('.panel')
		.removeClass('panel-success')
		.removeClass('panel-primary')
		.removeClass('panel-transpblue')
		.removeClass('panel-transporange')
		.addClass('panel-success');
	piechart.updateFontColor(COLOR_TEXT);
	barchart.updateFontColor(COLOR_TEXT);
});
theme.init('shipment');
	</script>
</html>
