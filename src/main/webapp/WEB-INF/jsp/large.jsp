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
	</head>
	<body>
		<h1>看板</h1>

		<svg id="asrs-view"></svg>

		<script type="text/javascript">

var asrs = asrsView({
	viewId: 'asrs-view',
	viewWidth: 1000, // pixels
	cols: 100,
	rowPairsPerGroup: 4,
	rowGroups: 4,
	rowGroupMargin: 30, // pixels
	pilerColor: '#0e83e4',
	trackColor: '#0e83e4',
	locBorderColor: '#0f4dcb',
	emptyLocColor: '#b8ddfc'
});


		</script>
	</body>
</html>