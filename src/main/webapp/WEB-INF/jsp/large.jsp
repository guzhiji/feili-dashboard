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
h1 {
	text-align: center;
	color: #a5c8e6;
	font-size: 30px;
	padding: 0;
	margin: 10px 0;
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
				<div class="panel panel-primary">
					<div class="panel-heading">
						入库信息
					</div>
					<div class="panel-body">
						abcabc
					</div>
				</div>
			</div>
			<div class="col-md-6" id="dash-col-2">
				<h1>立库概况</h1>
				<hr />
				<svg id="asrs-view" width="500" height="500"></svg>
				<hr />
				<div class="panel panel-primary">
					<div class="panel-heading">
						日常拣货订单总量
					</div>
					<div class="panel-body">
					</div>
				</div>
			</div>
			<div class="col-md-3">
				<div id="colors"></div>
				<div class="panel panel-primary">
					<div class="panel-heading">
						出库信息
					</div>
					<div class="panel-body">
					</div>
				</div>
			</div>
		</div>
		<script type="text/javascript">

function estimateAsrsView() {
	var w = $(window),
		estAsrsH = (w.height() - 153) / 2 - 80,
		estAsrsW = $('#dash-col-2').width(),
		asrsSvg = $('#asrs-view');
	asrsSvg.attr('width', estAsrsW);
	asrsSvg.attr('height', estAsrsH < 200 ? 200 : estAsrsH);
}
estimateAsrsView();

var asrs = asrsView({
	viewId: 'asrs-view',
	cols: 100,
	rowPairsPerGroup: 6,
	rowGroups: 1,
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
}, 500);
setInterval(function() {
	asrs.retrieve(Math.floor(Math.random() * 100), Math.floor(Math.random() * 32));
}, 200);

var colors = colorRange('#4c91cb', '#ae4369', 10);
for (var i in colors) {
	var c = document.createElement('div');
	c.setAttribute('style', 'height: 20px; background-color:'+ colors[i]);
	document.getElementById('colors').appendChild(c);
}

$(window).on('resize', function() {
	estimateAsrsView();
	asrs.resize();
});

		</script>
	</body>
</html>
