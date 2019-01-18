<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html;charset=utf-8">
		<title>看板 - 数据库性能</title>
		<link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
		<link href="/build/styles.min.css?1547705916" rel="stylesheet">
		<script src="/webjars/jquery/jquery.min.js"></script>
		<script src="/webjars/sockjs-client/sockjs.min.js"></script>
		<script src="/echarts.min.js"></script>
		<script src="/build/common.js?1547630069"></script>
		<script src="/build/theme.js?1547630069"></script>
	</head>
	<body>
		<h1>数据库性能看板</h1>
		<div class="row">
			<div class="col-md-12">
				<div class="panel panel-primary">
					<div class="panel-heading">
						查询延迟实时数据
					</div>
					<div class="panel-body">
						<div id="realtime-line-chart" style="height: 300px;"></div>
					</div>
				</div>
			</div>
			<div class="col-md-6">
				<div class="panel panel-primary">
					<div class="panel-heading">
						查询延迟分钟级历史数据
					</div>
					<div class="panel-body">
						<ul id="minutely-chart-mode" class="nav nav-pills" role="tablist">
							<li role="presentation" class="active mode-mean"><a href="#">平均值</a></li>
							<li role="presentation" class="mode-median"><a href="#">中位数</a></li>
							<li role="presentation" class="mode-ninetiethPercentile"><a href="#">90%值</a></li>
							<li role="presentation" class="mode-tenthPercentile"><a href="#">10%值</a></li>
							<li role="presentation" class="mode-max"><a href="#">最大值</a></li>
							<li role="presentation" class="mode-min"><a href="#">最小值</a></li>
						</ul>
						<div id="minutely-line-chart" style="height: 300px;"></div>
					</div>
				</div>
			</div>
			<div class="col-md-6">
				<div class="panel panel-primary">
					<div class="panel-heading">
						查询延迟小时级历史数据
					</div>
					<div class="panel-body">
						<ul id="hourly-chart-mode" class="nav nav-pills" role="tablist">
							<li role="presentation" class="active mode-mean"><a href="#">平均值</a></li>
							<li role="presentation" class="mode-median"><a href="#">中位数</a></li>
							<li role="presentation" class="mode-ninetiethPercentile"><a href="#">90%值</a></li>
							<li role="presentation" class="mode-tenthPercentile"><a href="#">10%值</a></li>
							<li role="presentation" class="mode-max"><a href="#">最大值</a></li>
							<li role="presentation" class="mode-min"><a href="#">最小值</a></li>
						</ul>
						<div id="hourly-line-chart" style="height: 300px;"></div>
					</div>
				</div>
			</div>
		</div>
		<div id="error-message" class="alert alert-danger">
			服务器连接错误
		</div>
	</body>
	<script type="text/javascript">

function toMinute(t) {
	var d = new Date(t);
	d.setMilliseconds(0);
	d.setSeconds(0);
	return d.getTime();
}

function toHour(t) {
	var d = new Date(t);
	d.setMilliseconds(0);
	d.setSeconds(0);
	d.setMinutes(0);
	return d.getTime();
}

function zeroPadded(value) {
	var n = value + '';
	if (n.length == 1)
		return '0' + n;
	return n;
}

function formatTime(value) {
	var d = new Date(value);
	return zeroPadded(d.getHours()) + ':' +
		zeroPadded(d.getMinutes()) + ':' +
		zeroPadded(d.getSeconds()) + ' \n' +
		zeroPadded(d.getMonth() + 1) + '-' +
		zeroPadded(d.getDate());
}

var sources = {
	'consolidation!order-trolley': '集货：订单-台车',
	'shipment!trolleys': '出货：台车',
	'shipment!trolley-order': '出货：台车-订单',
	'shipment!appointments': '出货：预约'
};
var realtimechart = LineChart('realtime-line-chart', 120, '{value}', formatTime, false, sources);
var minutelychart = LineChart('minutely-line-chart', 120, '{value}', null, false, sources);
var hourlychart = LineChart('hourly-line-chart', 120, '{value}', null, false, sources);
var modes = ['mean', 'median', 'ninetiethPercentile',
		'tenthPercentile', 'max', 'min'],
	minutelyMode = 'mean', hourlyMode = 'mean',
	minutelyData = [], hourlyData = [],
	currentMinute = null, currentHour = null;

function preprocessRealtimeData(data) {
	var out = [], series, dataItem;
	for (var key in data) {
		series = data[key];
		for (var i = 0; i < series.length; i++) {
			for (var t in series[i]) {
				dataItem = {};
				dataItem[key] = series[i][t];
				out.push({
					time: parseInt(t),
					data: dataItem
				});
			}
		}
	}
	return out;
}

function preprocessAggData(data, mode) {
	var out = [], dataTime = [], dataItems = {}, series;
	var i = 0, hasData;
	do {
		hasData = false;
		for (var key in data) {
			series = data[key];
			if (i in series) {
				hasData = true;
				if (series[i].time in dataItems) {
					dataItems[series[i].time][key] = series[i][mode];
				} else {
					var dataItem = {};
					dataItem[key] = series[i][mode];
					dataItems[series[i].time] = dataItem;
					dataTime.push(series[i].time);
				}
			}
		}
		i++;
	} while (hasData);
	for (i = 0; i < dataTime.length; i++) {
		out.push({
			time: dataTime[i],
			data: dataItems[dataTime[i]]
		});
	}
	return out;
}

function renderMinutelyData() {
	minutelychart.load(preprocessAggData(minutelyData, minutelyMode));
}

function renderHourlyData() {
	hourlychart.load(preprocessAggData(hourlyData, hourlyMode));
}

function loadMinutelyData() {
	$.get('/performance/data/minutely.json', function(data) {
		minutelyData = data;
		renderMinutelyData();
	});
}

function loadHourlyData() {
	$.get('/performance/data/hourly.json', function(data) {
		hourlyData = data;
		renderHourlyData();
	});
}

var chartFontSize = estimateChartFontSize();
realtimechart.updateFontSize(chartFontSize);
minutelychart.updateFontSize(chartFontSize);
hourlychart.updateFontSize(chartFontSize);

var connected = false;
var ws = null;
function connect() {
	ws = new SockJS('/sockjs/performance');
	ws.onmessage = function (evt) {
		var arr = evt.data.split(':');
		if (arr.length) {
			if (arr[0] in sources) {
				var t = parseInt(arr[1]),
					min = toMinute(t),
					hr = toHour(t),
					m = parseInt(arr[2]),
					p = {};
				p[arr[0]] = m;
				realtimechart.update(t, p);
				if (min != currentMinute) {
					currentMinute = min;
					loadMinutelyData();
					loadHourlyData();
				}
				if (hr != currentHour) {
					currentHour = hr;
					loadHourlyData();
				}
			}
		}
	};
	ws.onopen = function (evt) {
		connected = true;
		$('#error-message').fadeOut();
		$.get('/performance/data/realtime.json', function(data) {
			realtimechart.load(preprocessRealtimeData(data));
		});
		loadMinutelyData();
		loadHourlyData();
	};
	ws.onclose = function (evt) {
		var e = $('#error-message'), w = $(window);
		e.css({
			top: (w.height() - e.height()) / 2,
			left: (w.width() - e.width()) / 2
		}).fadeIn();
		realtimechart.clear();
		connected = false;
		setTimeout(connect, 1000);
	};
}
connect();

$('#minutely-chart-mode > li > a').on('click', function(e) {
	e.preventDefault();
	var listItem = $(this).parent();
	listItem.parent().find('>li').removeClass('active');
	listItem.addClass('active');
	for (var m = 0; m < modes.length; m++) {
		if (listItem.hasClass('mode-' + modes[m])) {
			minutelyMode = modes[m];
			break;
		}
	}
	renderMinutelyData();
});

$('#hourly-chart-mode > li > a').on('click', function(e) {
	e.preventDefault();
	var listItem = $(this).parent();
	listItem.parent().find('>li').removeClass('active');
	listItem.addClass('active');
	for (var m = 0; m < modes.length; m++) {
		if (listItem.hasClass('mode-' + modes[m])) {
			hourlyMode = modes[m];
			break;
		}
	}
	renderHourlyData();
});

$(window).on('resize', function() {
	realtimechart.rebind('realtime-line-chart');
	minutelychart.rebind('minutely-line-chart');
	hourlychart.rebind('hourly-line-chart');

	var chartFontSize = estimateChartFontSize();
	realtimechart.updateFontSize(chartFontSize);
	minutelychart.updateFontSize(chartFontSize);
	hourlychart.updateFontSize(chartFontSize);

	realtimechart.render();
	minutelychart.render();
	hourlychart.render();
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
	realtimechart.updateFontColor(COLOR_TEXT);
	minutelychart.updateFontColor(COLOR_TEXT);
	hourlychart.updateFontColor(COLOR_TEXT);
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
	realtimechart.updateFontColor('#612d07');
	minutelychart.updateFontColor('#612d07');
	hourlychart.updateFontColor('#612d07');
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
	realtimechart.updateFontColor(COLOR_TEXT);
	minutelychart.updateFontColor(COLOR_TEXT);
	hourlychart.updateFontColor(COLOR_TEXT);
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
	realtimechart.updateFontColor(COLOR_TEXT);
	minutelychart.updateFontColor(COLOR_TEXT);
	hourlychart.updateFontColor(COLOR_TEXT);
});
theme.init('performance');
	</script>
</html>
