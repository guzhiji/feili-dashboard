<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html;charset=utf-8">
		<title>看板</title>
		<link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
		<link href="/common.css" rel="stylesheet">
		<script src="/webjars/jquery/jquery.min.js"></script>
		<script src="/webjars/sockjs-client/sockjs.min.js"></script>
		<script src="/echarts.min.js"></script>
		<script src="/common.js"></script>
		<script src="/asrs-view.js"></script>
		<style>
body {
	background-color: #030708;
}
h1 {
	text-align: center;
	color: #a5c8e6;
	font-size: 3.5em;
	padding: 0;
	margin: 10px 0;
	text-shadow: 5px 5px 15px #334d71;
}

.panel-primary {
	background-color: #112a30;
	border: 0;
	border-radius: 10px;
}
.panel-primary > .panel-heading {
	font-size: 2.5em;
	text-shadow: 2px 2px 8px #0e2227;
	background-color: #245c6b;
	border-top-left-radius: 10px;
	border-top-right-radius: 10px;
}
.panel-primary > .panel-body {
	border-radius: 10px;
	background-color: #112a30;
}
#block-pick-info > .panel-body,
#block-in-kpi > .panel-body {
	padding: 3em;

}

.panel-warning {
	background-color: #0e172d;
	color: #fff;
	font-size: 2em;
	border-width: 0;
    box-shadow: 2px 2px 15px #3d296c;
}
.panel-warning > .panel-heading {
	background-color: #081518;
	border: 0;
	color: #9ddfe7;
}

.panel-info {
	background-color: #50382d;
	border-color: #452617;
	border-top-left-radius: 20px;
	border-top-right-radius: 20px;
}
.panel-info > .panel-heading {
	background-color: #452617;
	border-color: #452617;
	border-top-left-radius: 20px;
	border-top-right-radius: 20px;
	text-align: center;
	font-size: 2.5em;
	color: #ddd;
	text-shadow: 2px 2px #684e4e;
}
.panel-info > .panel-footer {
	background-color: #452617;
	border-color: #452617;
	font-size: 2em;
}
.panel-info > .panel-footer .list-group > .list-group-item {
	border-radius: 10px;
	margin: 15px 0;
	padding: 2em;
	box-shadow: 5px 5px 14px #310e0e;
	background-color: #1e1414;
	border-width: 0;
	color: #ffddd4;
}

.panel-default {
	background-color: #288faa;
	border-color: #288faa;
	border-radius: 15px;
	border-width: 5px;
	margin-top: 50px;
}
.panel-default > .panel-body {
	padding: 5em;
	background-color: #1b0008;
	border-color: #288faa;
	border-radius: 12px;
}
.panel-default > .panel-footer {
	background-color: #288faa;
	border-color: #288faa;
	color: #aee8ff;
	font-size: 2.5em;
	text-align: center;
	text-shadow: 2px 2px 16px #1f5d6b;
}
svg#asrs-view {
	margin: auto;
	display: block;
}

		</style>
	</head>
	<body>
		<div class="row">
			<div class="col-md-3">
				<div class="panel panel-primary" id="block-in-kpi">
					<div class="panel-heading">
						入库KPI
					</div>
					<div class="panel-body">
						<div id="line-in-kpi" style="height: 500px;"></div>
						<script>
		var lineInKpi = echarts.init(document.getElementById('line-in-kpi'));
		lineInKpi.setOption({
			xAxis: {
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
			},
			yAxis: {
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
			},
			series: [{
				data: [820, 932, 901, 934, 1290, 1330, 1320],
				type: 'line'
			}]
		});
						</script>
					</div>
				</div>
				<div class="panel panel-info" id="block-in-info">
					<div class="panel-heading">
						入库信息
					</div>
					<div class="panel-body">
						<div id="line-in-info" style="height: 400px;"></div>
						<script>
		var lineInInfo = echarts.init(document.getElementById('line-in-info'));
		lineInInfo.setOption({
			xAxis: {
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
			},
			yAxis: {
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
			},
			series: [{
				data: [820, 932, 901, 934, 1290, 1330, 1320],
				type: 'line'
			}]
		});
						</script>
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
			<div class="col-md-6">
				<h1>立库概况</h1>
				<div class="panel panel-default" id="block-asrs-view">
					<div class="panel-body">
						<svg id="asrs-view" width="500" height="500"></svg>
					</div>
					<div class="panel-footer">
						总容量：x箱 实箱数：y箱
					</div>
				</div>
				<div class="panel panel-primary" id="block-pick-info">
					<div class="panel-heading">
						拣货信息
					</div>
					<div class="panel-body">
						<div id="bar-pick-info" style="height: 400px;"></div>
						<script>
	var barPickInfo = echarts.init(document.getElementById('bar-pick-info'));
	barPickInfo.setOption({
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
				name: '直接访问',
				type: 'bar',
				data: [10, 52, 200, 334, 390, 330, 220]
			},
			{
				name: '直接访问2',
				type: 'bar',
				data: [10, 52, 200, 334, 390, 330, 220]
			}
		]
	});
						</script>
					</div>
				</div>
			</div>
			<div class="col-md-3">
				<div class="panel panel-primary" id="block-error-info">
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
						<script>
	var pieOutInfo = echarts.init(document.getElementById('pie-out-info'));
	pieOutInfo.setOption({
		tooltip: {
			trigger: 'item',
			formatter: "{a} <br/>{b}: {c} ({d}%)"
		},
		legend: {
			textStyle: {
				color: COLOR_TEXT,
				fontSize: 25
			},
			// x: 'left',
			data: ['直接访问', '邮件营销', '联盟广告', '视频广告', '搜索引擎']
		},
		series: [
			{
				name: '访问来源',
				type: 'pie',
				radius: '50%',
				label: {
					color: COLOR_TEXT,
					fontSize: 25
				},
				labelLine: {
					normal: {
						show: false
					}
				},
				data: [
					{ value: 335, name: '直接访问' },
					{ value: 310, name: '邮件营销' },
					{ value: 234, name: '联盟广告' },
					{ value: 135, name: '视频广告' },
					{ value: 1548, name: '搜索引擎' }
				]
			}
		]
	});
						</script>
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

function estimateAsrsView() {
	var w = $(window),
		estAsrsH = (w.height() - 153) / 2 - 80,
		estAsrsW = $('#block-asrs-view .panel-body').width(),
		asrsSvg = $('#asrs-view');
	asrsSvg.attr('width', estAsrsW);
	asrsSvg.attr('height', estAsrsH < 200 ? 200 : estAsrsH);
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
});

		</script>
	</body>
</html>
