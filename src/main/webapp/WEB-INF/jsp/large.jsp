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

#block-pick-info > .panel-body,
#block-in-kpi > .panel-body {
	padding: 3em;

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
				<div class="panel panel-success" id="block-in-kpi">
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
				<div class="panel panel-success" id="block-pick-info">
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
