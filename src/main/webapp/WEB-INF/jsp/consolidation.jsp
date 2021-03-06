<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html;charset=utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
		<title>看板 - 集货</title>
		<link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
		<link href="/build/styles.min.css?1548896602" rel="stylesheet">
		<link rel="shortcut icon" href="/favicon.ico" type="image/x-icon">
		<script src="/webjars/jquery/jquery.min.js"></script>
		<script src="/webjars/sockjs-client/sockjs.min.js"></script>
		<script src="/webjars/stomp-websocket/stomp.min.js"></script>
		<script src="/echarts.min.js"></script>
		<script src="/build/common.js?1548896602"></script>
		<script src="/build/theme.js?1548079168"></script>
	</head>
	<body>
		<h1>
			集货看板
			<a href="#" id="option-btn"><i class="glyphicon glyphicon-option-vertical"></i></a>
		</h1>
		<div class="row">
			<div class="col-xs-4">
				<div class="panel panel-primary">
					<div class="panel-heading">
						集货订单状态
					</div>
					<div class="panel-body">
						<div id="pie-chart" style="height: 300px;"></div>
					</div>
				</div>
			</div>
			<div class="col-xs-8">
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
			<div class="col-xs-12">
				<div class="panel panel-primary">
					<div class="panel-heading">
						台车-订单（未发货）信息
						<div id="table-page">1/1</div>
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

function estimateChartSizes() {
	var w = $(window),
		estHeight = (w.height() - 200) / 3 - 80,
		qPieChart = $('#pie-chart'),
		qLineChart = $('#line-chart');
	if (estHeight < 250) estHeight = 250;
	qPieChart.height(estHeight);
	qLineChart.height(estHeight);
}
estimateChartSizes();
var piechart = PieChart('pie-chart', '单数', {
	PICKED: '拣货完成',
	SHIPPED: '发货完成',
	OTHER: '拣货中'
});
var linechart = LineChart('line-chart', 24, '{value}', null, true, {
	PICKED: '拣货完成',
	SHIPPED: '发货完成',
	OTHER: '其它（开始拣货）'
});
var datatable = DataTable('data-table', 'table-page', 5000, [
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
	if (values && values.sort) {
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
var stomp = null;
function connect() {
	var ws = new SockJS('/sockjs');
	stomp = Stomp.over(ws);
	stomp.debug = null;
	stomp.connect({}, function() {
		connected = true;
		$('#error-message').fadeOut();
		$.get('/consolidation/table.json', updateTableData).done(datatable.render);
		$.get('/consolidation/history.json', linechart.load);
		$.get('/consolidation/status.json', piechart.update);
		stomp.subscribe('/dashboard/consolidation', function(msg) {
			var arr = msg.body.split(':');
			if (arr.length) {
				if (arr[0] == 'pie') {
					piechart.update(deserializeMessage(arr[1], parseInt));
				} else if (arr[0] == 'line') {
					var t = parseInt(arr[1]);
					linechart.update(t, deserializeMessage(arr[2], parseInt));
				} else if (arr[0] == 'init') {
					$.get('/consolidation/table.json', updateTableData).done(datatable.render);
					$.get('/consolidation/history.json', linechart.load);
				} else if (arr[0] == 'reload') {
					window.location.reload();
				} else if (arr[0] == 'basetime') {
					setBaseTime(arr[1]);
				}
			}
		});
	}, function() {
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
	});
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

theme.register('transpgreen', '绿色透明风格', function() {
	$('body')
		.removeClass('transpblue')
		.removeClass('transporange')
		.removeClass('transpgreen')
		.addClass('transpgreen');
	$('.panel')
		.removeClass('panel-success')
		.removeClass('panel-primary')
		.removeClass('panel-transpblue')
		.removeClass('panel-transporange')
		.removeClass('panel-transpgreen')
		.addClass('panel-transpgreen');
	piechart.updateFontColor('#c9fcc9');
	linechart.updateFontColor('#c9fcc9');
});
theme.register('transpblue', '蓝色透明风格', function() {
	$('body')
		.removeClass('transpblue')
		.removeClass('transporange')
		.removeClass('transpgreen')
		.addClass('transpblue');
	$('.panel')
		.removeClass('panel-success')
		.removeClass('panel-primary')
		.removeClass('panel-transpblue')
		.removeClass('panel-transporange')
		.removeClass('panel-transpgreen')
		.addClass('panel-transpblue');
	piechart.updateFontColor(COLOR_TEXT);
	linechart.updateFontColor(COLOR_TEXT);
});
theme.register('transporange', '橘色透明风格', function() {
	$('body')
		.removeClass('transpblue')
		.removeClass('transporange')
		.removeClass('transpgreen')
		.addClass('transporange');
	$('.panel')
		.removeClass('panel-success')
		.removeClass('panel-primary')
		.removeClass('panel-transpblue')
		.removeClass('panel-transporange')
		.removeClass('panel-transpgreen')
		.addClass('panel-transporange');
	piechart.updateFontColor('#612d07');
	linechart.updateFontColor('#612d07');
});
theme.register('blue', '蓝色风格', function() {
	$('body')
		.removeClass('transpblue')
		.removeClass('transporange')
		.removeClass('transpgreen');
	$('.panel')
		.removeClass('panel-success')
		.removeClass('panel-primary')
		.removeClass('panel-transpblue')
		.removeClass('panel-transporange')
		.removeClass('panel-transpgreen')
		.addClass('panel-primary');
	piechart.updateFontColor(COLOR_TEXT);
	linechart.updateFontColor(COLOR_TEXT);
});
theme.register('green', '绿色风格', function() {
	$('body')
		.removeClass('transpblue')
		.removeClass('transporange')
		.removeClass('transpgreen');
	$('.panel')
		.removeClass('panel-success')
		.removeClass('panel-primary')
		.removeClass('panel-transpblue')
		.removeClass('panel-transporange')
		.removeClass('panel-transpgreen')
		.addClass('panel-success');
	piechart.updateFontColor(COLOR_TEXT);
	linechart.updateFontColor(COLOR_TEXT);
});
theme.init('consolidation', '#option-btn');
	</script>
</html>
