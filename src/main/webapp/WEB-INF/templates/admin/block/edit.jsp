<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="content-type" content="text/html;charset=utf-8">
        <title>test</title>
        <link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
        <script src="/webjars/jquery/jquery.min.js"></script>
        <script src="/webjars/bootstrap/js/bootstrap.min.js"></script>
    </head>
    <body>
        <div class="container">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    看板单元块
                </div>
                <div class="panel-body">
                    <form class="form-horizontal" id="form-block" method="POST"
                        action="${saveUrl}">
                        <div class="form-group">
                            <label for="input-name" class="col-md-2 control-label">名称</label>
                            <div class="col-md-10">
                                <input type="text" name="name" id="input-name" class="form-control" value="${entity.name}" />
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="input-width" class="col-md-2 control-label">宽度</label>
                            <div class="col-md-10">
                                <input type="text" name="width" id="input-width" class="form-control" value="${entity.width}" />
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="input-min-height" class="col-md-2 control-label">最小高度</label>
                            <div class="col-md-10">
                                <input type="text" name="minHeight" id="input-min-height" class="form-control" value="${entity.minHeight}" />
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="col-md-offset-2 col-md-10">
                                <div class="checkbox">
                                    <label>
                                        <c:if test="${entity.active}">
                                            <input type="checkbox" name="active" checked="checked" />
                                        </c:if>
                                        <c:if test="${not entity.active}">
                                            <input type="checkbox" name="active" />
                                        </c:if>
                                        是否启用
                                    </label>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="select-data-renderer" class="col-md-2 control-label">数据展示方式</label>
                            <div class="col-md-10">
                                <select name="dataRenderer" id="select-data-renderer" class="form-control">
                                    <option>-</option>
                                    <c:forEach items="${dataRenderers}" var="dr">
                                        <c:choose>
                                            <c:when test="${dr == entity.dataRenderer}">
                                                <option value="${dr}" selected="selected">${dr}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${dr}">${dr}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="select-data-source" class="col-md-2 control-label">数据源</label>
                            <div class="col-md-10">
                                <select name="dataSourceId" id="select-data-source" class="form-control">
                                    <option>-</option>
                                    <c:forEach items="${dataSources}" var="ds">
                                        <c:choose>
                                            <c:when test="${ds.id == entity.dataSource.id}">
                                                <option value="${ds.id}" selected="selected">${ds.name}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${ds.id}">${ds.name}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="select-data-preprocessor" class="col-md-2 control-label">数据预处理方式</label>
                            <div class="col-md-10">
                                <select name="dataPreprocessor" id="select-data-preprocessor" class="form-control">
                                    <option>-</option>
                                    <c:forEach items="${dataPreprocessors}" var="dp">
                                        <c:choose>
                                            <c:when test="${dp == entity.dataPreprocessor}">
                                                <option value="${dp}" selected="selected">${dp}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${dp}">${dp}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="select-msg-notifier" class="col-md-2 control-label">数据推送源</label>
                            <div class="col-md-10">
                                <select name="messageNotifierId" id="select-msg-notifier" class="form-control">
                                    <option>-</option>
                                    <c:forEach items="${messageNotifiers}" var="mn">
                                        <c:choose>
                                            <c:when test="${mn.id == entity.messageNotifier.id}">
                                                <option value="${mn.id}" selected="selected">${mn.name}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${mn.id}">${mn.name}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="select-msg-handler" class="col-md-2 control-label">数据推送接收器</label>
                            <div class="col-md-10">
                                <select name="messageHandler" id="select-msg-handler" class="form-control">
                                    <option>-</option>
                                    <c:forEach items="${messageHandlers}" var="mh">
                                        <c:choose>
                                            <c:when test="${mh == entity.messageHandler}">
                                                <option value="${mh}" selected="selected">${mh}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${mh}">${mh}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                    </form>
                </div>
                <div class="panel-footer">
                    <button type="button" id="btn-save" class="btn btn-primary">创建单元块</button>
                    <a class="btn btn-default" href="/admin/dashboards/${parent.id}/blocks">返回</a>
                </div>
            </div>
        </div>
        <script type="text/javascript">
$('#btn-save').on('click', function() {
    $('#form-block').submit();
});
        </script>
    </body>
</html>
