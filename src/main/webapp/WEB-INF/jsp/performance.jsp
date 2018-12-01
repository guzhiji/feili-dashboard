<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="content-type" content="text/html;charset=utf-8">
        <title>看板 - 数据库性能</title>
        <link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
        <link href="/common.css" rel="stylesheet">
        <script src="/webjars/jquery/jquery.min.js"></script>
        <script src="/webjars/sockjs-client/sockjs.min.js"></script>
        <script src="/echarts.min.js"></script>
        <script src="/common.js"></script>
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
                        <ul class="nav nav-pills" role="tablist">
                            <li role="presentation" class="active"><a href="#">平均值</a></li>
                            <li role="presentation"><a href="#">中位数</a></li>
                            <li role="presentation"><a href="#">90%值</a></li>
                            <li role="presentation"><a href="#">10%值</a></li>
                            <li role="presentation"><a href="#">最大值</a></li>
                            <li role="presentation"><a href="#">最小值</a></li>
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
                        <ul class="nav nav-pills" role="tablist">
                            <li role="presentation" class="active"><a href="#">平均值</a></li>
                            <li role="presentation"><a href="#">中位数</a></li>
                            <li role="presentation"><a href="#">90%值</a></li>
                            <li role="presentation"><a href="#">10%值</a></li>
                            <li role="presentation"><a href="#">最大值</a></li>
                            <li role="presentation"><a href="#">最小值</a></li>
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

var sources = {
    'consolidation!order-trolley': '集货：订单-台车',
    'shipment!trolleys': '出货：台车',
    'shipment!trolley-order': '出货：台车-订单',
    'shipment!appointments': '出货：预约'
};
var realtimechart = LineChart('realtime-line-chart', '{value}', sources);
var minutelychart = LineChart('minutely-line-chart', '{value}', sources);
var hourlychart = LineChart('hourly-line-chart', '{value}', sources);

var connected = false;
var ws = null;
function connect() {
    ws = new SockJS('/sockjs/performance');
    ws.onmessage = function (evt) {
        var arr = evt.data.split(':');
        if (arr.length) {
            if (arr[0] in sources) {
                var t = parseInt(arr[1]),
                    m = parseInt(arr[2]),
                    p = {};
                p[arr[0]] = m;
                realtimechart.update(t, p);
            }
        }
    };
    ws.onopen = function (evt) {
        connected = true;
        $('#error-message').fadeOut();
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

    </script>
</html>
