<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html;charset=utf-8">
		<title>看板 - 集货</title>
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

function showError() {
	var e = $('#error-message'), w = $(window);
	e.css({
		top: (w.height() - e.height()) / 2,
		left: (w.width() - e.width()) / 2
	}).show();
	piechart.clear();
	linechart.clear();
	datatable.clear();
}

function hideError() {
	$('#error-message').hide();
}

function updateData(done) {
	$.get('/consolidation/table.json', updateTableData).done(hideError).done(done).fail(showError);
	$.get('/consolidation/status.json', piechart.update).done(hideError).fail(showError);
	$.get('/consolidation/history.json', linechart.load).done(hideError).fail(showError);
}

setInterval(updateData, 5000);
updateData(datatable.render);

$(window).on('resize', function() {
	var w = $(window),
		estHeight = (w.height() - 200) / 3 - 80,
		qPieChart = $('#pie-chart'),
		qLineChart = $('#line-chart');
	if (estHeight < 300) estHeight = 300;
	qPieChart.height(estHeight);
	qLineChart.height(estHeight);
	piechart.rebind('pie-chart');
	linechart.rebind('line-chart');
	piechart.render();
	linechart.render();
});

	</script>
</html>
