<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html;charset=utf-8">
		<title>看板 - 集货</title>
		<link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
		<link href="/common.css?1547522718" rel="stylesheet">
		<script src="/webjars/jquery/jquery.min.js"></script>
		<script src="/webjars/sockjs-client/sockjs.min.js"></script>
		<script src="/echarts.min.js"></script>
		<script src="/common.js?1547522718"></script>
	</head>
	<body>
		<h1>集货看板</h1>
		<div class="row">
			<div class="col-md-4">
				<div class="panel panel-primary">
					<div class="panel-heading">
						集货订单状态
					</div>
					<div class="panel-body">
						<div id="pie-chart" style="height: 300px;"></div>
					</div>
				</div>
			</div>
			<div class="col-md-8">
				<div class="panel panel-primary">
					<div class="panel-heading">
						今日集货订单量
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
						台车信息
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
		<ul class="dropdown-menu" id="context-menu">
			<li><a href="#" id="menu-theme-blue">蓝色风格</a></li>
			<li><a href="#" id="menu-theme-green">绿色风格</a></li>
			<li><a href="#" id="menu-theme-transpblue">蓝色透明风格</a></li>
			<li role="separator" class="divider"></li>
			<li><a href="#" id="menu-refresh">刷新</a></li>
			<!-- <li><a href="#" id="menu-fullscreen">全屏</a></li> -->
		</ul>
	</body>
	<script type="text/javascript">

function estimateChartSizes() {
	var w = $(window),
		estHeight = (w.height() - 200) / 3 - 80,
		qPieChart = $('#pie-chart'),
		qLineChart = $('#line-chart');
	if (estHeight < 300) estHeight = 300;
	qPieChart.height(estHeight);
	qLineChart.height(estHeight);
}
estimateChartSizes();
var piechart = PieChart('pie-chart', '单数', {
	PICKED: '已拣货',
	SHIPPED: '已发货',
	OTHER: '其它'
});
var linechart = LineChart('line-chart', 24, '{value}', null, true, {
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

function updateTableData(values) {
	if (values.sort) {
		for (var i = 0; i < values.length; i++)
			values[i].timeRemaining = calcRemainingTime(values[i].shipDate);
		values.sort(function (a, b) {
			if (a.toCombine && !b.toCombine) return -1;
			if (!a.toCombine && b.toCombine) return 1;
			if (a.timeRemaining < b.timeRemaining) return -1;
			if (a.timeRemaining > b.timeRemaining) return 1;
			if (a.trolleyId < b.trolleyId) return -1;
			if (a.trolleyId > b.trolleyId) return 1;
			if (a.orderKey < b.orderKey) return -1;
			if (a.orderKey > b.orderKey) return 1;
			return 0;
		});
		datatable.update(values);
	}
}

var chartFontSize = estimateChartFontSize();
piechart.updateFontSize(chartFontSize);
linechart.updateFontSize(chartFontSize);

var connected = false;
var ws = null;
function connect() {
	ws = new SockJS('/sockjs/consolidation');
	ws.onmessage = function(evt) {
		var arr = evt.data.split(':');
		if (arr.length) {
			if (arr[0] == 'pie') {
				piechart.update(deserializeMessage(arr[1], parseInt));
			} else if (arr[0] == 'line') {
				var t = parseInt(arr[1]);
				linechart.update(t, deserializeMessage(arr[2], parseInt));
			} else if (arr[0] == 'init') {
				$.get('/consolidation/table.json', updateTableData).done(datatable.render);
				$.get('/consolidation/history.json', linechart.load);
			} else if (arr[0] == 'basetime') {
				setBaseTime(arr[1]);
			}
		}
	};
	ws.onopen = function(evt) {
		connected = true;
		$('#error-message').fadeOut();
		$.get('/consolidation/table.json', updateTableData).done(datatable.render);
		$.get('/consolidation/history.json', linechart.load);
		$.get('/consolidation/status.json', piechart.update);
	};
	ws.onclose = function(evt) {
		var e = $('#error-message'), w = $(window);
		e.css({
			top: (w.height() - e.height()) / 2,
			left: (w.width() - e.width()) / 2
		}).fadeIn();
		datatable.clear();
		linechart.clear();
		piechart.clear();
		connected = false;
		setTimeout(connect, 1000);
	};
}
connect();
setInterval(function() {
	if (connected) $.get('/consolidation/table.json', updateTableData);
}, 5000);
$(window).on('resize', function() {

	estimateChartSizes();

	piechart.rebind('pie-chart');
	linechart.rebind('line-chart');

	var chartFontSize = estimateChartFontSize();
	piechart.updateFontSize(chartFontSize);
	linechart.updateFontSize(chartFontSize);

	piechart.render();
	linechart.render();
});
$('#menu-theme-blue').on('click', function() {
	$('body').removeClass('transpblue');
	$('.panel')
		.removeClass('panel-success')
		.removeClass('panel-primary')
		.removeClass('panel-transpblue')
		.addClass('panel-primary');
	$('#context-menu').css('display', 'none');
	return false;
});
$('#menu-theme-green').on('click', function() {
	$('body').removeClass('transpblue');
	$('.panel')
		.removeClass('panel-success')
		.removeClass('panel-primary')
		.removeClass('panel-transpblue')
		.addClass('panel-success');
	$('#context-menu').css('display', 'none');
	return false;
});
$('#menu-theme-transpblue').on('click', function() {
	$('body').addClass('transpblue');
	$('.panel')
		.removeClass('panel-success')
		.removeClass('panel-primary')
		.removeClass('panel-transpblue')
		.addClass('panel-transpblue');
	$('#context-menu').css('display', 'none');
	return false;
});
$('#menu-refresh').on('click', function() {
	window.location.reload();
	return false;
});
$('#menu-fullscreen').on('click', function() {
	requestFullScreen(document.body);
	$('#context-menu').css('display', 'none');
	return false;
});
$("body")
	.on('click', function() {
		$('#context-menu').css('display', 'none');
	})
	.on('contextmenu', function(e) {
		$('#context-menu').css({
			display: 'block',
			left: e.pageX,
			top: e.pageY
		});
		return false;
	});
	</script>
</html>
