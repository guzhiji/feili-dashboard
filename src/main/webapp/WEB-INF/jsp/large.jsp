<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html;charset=utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
		<title>看板</title>
		<link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
		<link href="/build/styles.min.css?1547800911" rel="stylesheet">
		<script src="/webjars/jquery/jquery.min.js"></script>
		<script src="/webjars/sockjs-client/sockjs.min.js"></script>
		<script src="/echarts.min.js"></script>
		<script src="/build/common.js?1547800911"></script>
		<script src="/build/asrs-view.js?1547630069"></script>
		<script src="/build/theme.js?1547630069"></script>
		<style>

@media screen and (min-width: 3800px) {
	#block-pick-info > .panel-body,
	#block-in-kpi > .panel-body {
		padding: 3em;
	}
}
@media screen and (min-width: 1900px) and (max-width: 3799px) {
	#block-pick-info > .panel-body,
	#block-in-kpi > .panel-body {
		padding: 2em;
	}
}
@media screen and (min-width: 1000px) and (max-width: 1899px) {
	#block-pick-info > .panel-body,
	#block-in-kpi > .panel-body {
		padding: 2em;
	}
}

svg#asrs-view {
	margin: auto;
	display: block;
}

		</style>
	</head>
	<body>
		<div class="row">
			<div class="col-xs-3">
				<div class="panel panel-success" id="block-in-kpi">
					<div class="panel-heading">
						入库KPI
					</div>
					<div class="panel-body">
						<div id="line-in-kpi" style="height: 500px;"></div>
					</div>
				</div>
				<div class="panel panel-info" id="block-in-info">
					<div class="panel-heading">
						入库信息
					</div>
					<div class="panel-body">
						<div id="line-in-info" style="height: 400px;"></div>
					</div>
					<div class="panel-footer">
						<div class="list-group">
							<div class="list-group-item">
								<div>日收货量（LPN）： 120</div>
								<div>日入库量（LPN）： 90</div>
								<div>剩余待入库（LPN）： 30</div>
							</div>
							<div class="list-group-item">
								<ul>
									<li>sdfasas入库成功</li>
									<li>weroiupo入库成功</li>
									<li>weflklkj入库成功</li>
									<li>asfweoii入库成功</li>
								</ul>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-xs-6">
				<h1>立库概况</h1>
				<div class="panel panel-default" id="block-asrs-view">
					<div class="panel-body">
						<svg id="asrs-view" width="500" height="500"></svg>
					</div>
					<div class="panel-footer">
						总容量：x箱 实箱数：y箱
					</div>
				</div>
				<div class="panel panel-success" id="block-pick-info">
					<div class="panel-heading">
						拣货信息
					</div>
					<div class="panel-body">
						<div id="bar-pick-info" style="height: 400px;"></div>
						<script>
	var barPickInfo = echarts.init(document.getElementById('bar-pick-info'));
	barPickInfo.setOption({
		legend: {
			textStyle: {
				color: [COLOR_TEXT],
				fontSize: 25
			}
		},
		xAxis: [
			{
				splitLine: {
					lineStyle: {
						color: [COLOR_DARK_LINE]
					}
				},
				axisLine: {
					lineStyle: {
						color: COLOR_DARK_LINE
					}
				},
				axisLabel: {
					color: COLOR_TEXT,
					fontSize: 25
				},
				type: 'category',
				data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
			}
		],
		yAxis: [
			{
				type: 'value',
				splitLine: {
					lineStyle: {
						color: [COLOR_DARK_LINE]
					}
				},
				axisLine: {
					lineStyle: {
						color: COLOR_DARK_LINE
					}
				},
				axisLabel: {
					color: COLOR_TEXT,
					fontSize: 25
				}
			}
		],
		series: [
			{
				name: '直接',
				type: 'bar',
				data: [10, 52, 200, 334, 390, 330, 220]
			},
			{
				name: '间接',
				type: 'bar',
				data: [10, 52, 200, 334, 390, 330, 220]
			}
		]
	});
						</script>
					</div>
				</div>
			</div>
			<div class="col-xs-3">
				<div class="panel panel-success" id="block-error-info">
					<div class="panel-body">
						<div class="panel panel-warning">
							<div class="panel-heading">
								入库异常信息：
							</div>
							<div class="panel-body">
								<ul>
									<li>LPN: asdjlas 原因：称重异常</li>
									<li>LPN: sweoifj 原因：称重异常</li>
								</ul>
							</div>
						</div>
						<div class="panel panel-warning">
							<div class="panel-heading">
								出库异常信息：
							</div>
							<div class="panel-body">
								<ul>
									<li>SO: wqeoiijdf 原因：改单异常</li>
									<li>SO: weoiidfsljk 原因：改单异常</li>
								</ul>
							</div>
						</div>
						<div class="panel panel-warning">
							<div class="panel-heading">
								机械异常信息：
							</div>
							<div class="panel-body">
								<ul>
									<li>1号堆垛机异常</li>
								</ul>
							</div>
						</div>
					</div>
				</div>
				<div class="panel panel-info" id="block-out-info">
					<div class="panel-heading">
						出库信息
					</div>
					<div class="panel-body">
						<div id="pie-out-info" style="height: 400px;"></div>
					</div>
					<div class="panel-footer">
						<div class="list-group">
							<div class="list-group-item">
								<ul>
									<li>weoijsad出库成功</li>
									<li>asdwioi出库成功</li>
									<li>sdlajfl出库成功</li>
									<li>asdkjfl出库成功</li>
									<li>2isjkljl出库成功</li>
								</ul>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<script type="text/javascript">

var estimatedChartFontSize = estimateChartFontSize();
var pieOutInfo = PieChart('pie-out-info', '来源', {
	direct: '直接访问',
	mail: '邮件营销',
	allies : '联盟广告',
	video: '视频广告',
	search: '搜索引擎'
});
pieOutInfo.update({
	direct: 335,
	mail: 310,
	allies: 234,
	video: 135,
	search: 1558
});
pieOutInfo.updateFontSize(estimatedChartFontSize);
var lineInKpi = LineChart('line-in-kpi', 10, '{value}', null, true, {
	A: 'A'
});
(function () {
	var data = [];
	for (var i = 10; i >= 0; i--) {
		data.push({
			time: new Date().getTime() - 3600000 * i,
			data: { A: Math.random() }
		});
	}
	lineInKpi.load(data);
	lineInKpi.updateFontSize(estimatedChartFontSize);
})();
var lineInInfo = LineChart('line-in-info', 10, '{value}', null, true, {
	A: 'A',
	B: 'B',
	C: 'C'
});
(function () {
	var data = [];
	for (var i = 10; i >= 0; i--) {
		data.push({
			time: new Date().getTime() - 3600000 * i,
			data: { A: Math.random(), B: Math.random(), C: Math.random() }
		});
	}
	lineInInfo.load(data);
	lineInInfo.updateFontSize(estimatedChartFontSize);
})();

function estimateAsrsView() {
	var w = $(window),
		estAsrsH = (w.height() - 153) / 2 - 80,
		estAsrsW = $('#block-asrs-view .panel-body').width(),
		asrsSvg = $('#asrs-view');
	asrsSvg.attr('width', estAsrsW);
	asrsSvg.attr('height', estAsrsH < 600 ? 600 : estAsrsH);
}
estimateAsrsView();

var asrs = asrsView({
	viewId: 'asrs-view',
	cols: 100,
	rowPairsPerGroup: 4,
	rowGroups: 4,
	rowGroupMargin: 30, // pixels
	pilerColor: '#0e83e4',
	trackColor: '#0e83e4',
	locBorderColor: '#112348',
	locBlinkColor: '#fffc36',
	locEmptyColor: '#b8ddfc',
	locUtilizationColors: colorRange('#4c91cb', '#ae4369', 10)
});

setInterval(function() {
	asrs.store(Math.floor(Math.random() * 100), Math.floor(Math.random() * 32));
}, 200);
setInterval(function() {
	asrs.retrieve(Math.floor(Math.random() * 100), Math.floor(Math.random() * 32));
}, 1000);

$(window).on('resize', function() {

	estimateAsrsView();
	asrs.resize();

	pieOutInfo.rebind('pie-out-info');
	lineInKpi.rebind('line-in-kpi');
	lineInInfo.rebind('line-in-info');

	var estimatedChartFontSize = estimateChartFontSize();
	pieOutInfo.updateFontSize(estimatedChartFontSize);
	lineInKpi.updateFontSize(estimatedChartFontSize);
	lineInInfo.updateFontSize(estimatedChartFontSize);

	pieOutInfo.render();
	lineInKpi.render();
	lineInInfo.render();
});

theme.register('transpblue', '蓝色透明风格', function() {
	$('body')
		.removeClass('transpblue')
		.addClass('transpblue');
	$('#block-in-kpi,#block-pick-info,#block-error-info')
		.removeClass('panel-success')
		.removeClass('panel-transpblue')
		.addClass('panel-transpblue');
	$('#block-in-info,#block-out-info')
		.removeClass('panel-info')
		.removeClass('panel-transporange')
		.addClass('panel-transporange');
	/*
	$('#block-asrs-view')
		.removeClass('panel-default')
		.addClass('panel-default');
	*/
});
theme.register('opaque', '非透明风格', function() {
	$('body').removeClass('transpblue');
	$('#block-in-kpi,#block-pick-info,#block-error-info')
		.removeClass('panel-success')
		.removeClass('panel-transpblue')
		.addClass('panel-success');
	$('#block-in-info,#block-out-info')
		.removeClass('panel-info')
		.removeClass('panel-transporange')
		.addClass('panel-info');
	/*
	$('#block-asrs-view')
		.removeClass('panel-default')
		.addClass('panel-default');
	*/
});
theme.init('large');
		</script>
	</body>
</html>
