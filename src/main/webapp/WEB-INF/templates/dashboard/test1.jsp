<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="content-type" content="text/html;charset=utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
        <title>看板 - ${dashboard.name}</title>
        <link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
        <link href="/build/styles.min.css?1548312135" rel="stylesheet">
        <link rel="shortcut icon" href="/favicon.ico" type="image/x-icon">
        <script src="/webjars/jquery/jquery.min.js"></script>
        <script src="/webjars/sockjs-client/sockjs.min.js"></script>
        <script src="/webjars/stomp-websocket/stomp.min.js"></script>
        <script src="/echarts.min.js"></script>
        <script src="/build/main.min.js?1548139581"></script>
    </head>
    <body>
        <h1>
            ${dashboard.name}
            <a href="#" id="option-btn"><i class="glyphicon glyphicon-option-vertical"></i></a>
        </h1>
        <div class="row">
            <c:forEach items="${dashboard.blocks}" var="blk">
            <c:if test="${blk.active}">
            <div class="col-xs-${blk.width}">
                <div class="panel panel-primary" id="block-${blk.id}">
                    <div class="panel-heading">
                        ${blk.name}
                    </div>
                    <div class="panel-body"
                        id="block-${blk.id}-container"
                        style="height: ${blk.minHeight}px;">
                    </div>
                </div>
            </div>
            </c:if>
            </c:forEach>
        </div>
        <div class="row">
            <div class="col-xs-12">
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
        <script type="text/javascript">
        var stomp = null;
        var stompConnected = false;
        var resultSources = {};
        var messageSources = {};
        var resultHandlers = {
            'resultHandler1': function(data) {
                console.log('resultHanlder1', data);
            },
            'resultHandler2': function(data) {
                console.log('resultHandler2', data);
            }
        };
        var messageHandlers = {
            'msgh1': function(data) {
                console.log('msgh1', data);
            },
            'msgh2': function(data) {
                console.log('msgh2', data);
            },
            'msgh3': function(data) {
                console.log('msgh3', data);
            }
        };
        function init(data) {
            var b, blk, path;
            for (b = 0; b < data.blocks.length; b++) {
                blk = data.blocks[b];
                if (blk.monitorId) {
                    if (blk.resultSource) {
                        path = '/dashboard/monitor/' + blk.monitorId +
                            '/result/' + blk.resultSource + '.json';
                        resultSources[path] = resultHandlers[blk.resultHandler];
                    }
                    if (blk.messageSource) {
                        path = '/dashboard/monitor/' + blk.monitorId +
                            '/' + blk.messageSource;
                        messageSources[path] = messageHandlers[blk.messageHandler];
                    }
                }
            }
        }
        $.get('/dashboard/${dashboard.id}.json', function(data) {
            console.log(data);
            init(data);
            console.log(resultSources);
            console.log(messageSources);
            connectWs();
        });
        function connectWs() {
            var ws = new SockJS('/sockjs');
            stomp = Stomp.over(ws);
            stomp.debug = null;
            stomp.connect({}, function() {
                console.log('sockjs connected');
                // on success
                stompConnected = true;
                for (var dest in messageSources)
                    stomp.subscribe(dest, messageSources[dest]);
            }, function() {
                // on error
                stompConnected = false;
                setTimeout(connectWs, 1000);
            });
        }
        setInterval(function() {
            for (var url in resultSources) {
                $.get(url, resultSources[url]);
            }
        }, 5000);
        </script>
    </body>
</html>
