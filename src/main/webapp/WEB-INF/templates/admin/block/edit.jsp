<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="content-type" content="text/html;charset=utf-8">
        <title>
            <c:if test="${mode == 'create'}">
                看板单元块：创建
            </c:if>
            <c:if test="${mode == 'modify'}">
                看板单元块：${entity.name}
            </c:if>
        </title>
        <link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
        <script src="/webjars/jquery/jquery.min.js"></script>
        <script src="/webjars/bootstrap/js/bootstrap.min.js"></script>
    </head>
    <body>
        <div class="container">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <c:if test="${mode == 'create'}">
                        看板单元块：创建
                    </c:if>
                    <c:if test="${mode == 'modify'}">
                        看板单元块：${entity.name}（#${entity.id}）
                    </c:if>
                </div>
                <div class="panel-body">

                    <c:if test="${flashMessage == 'block-no-data'}">
                        <div class="alert alert-danger flash-message">请选择数据源或数据推送源</div>
                    </c:if>

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
                                    <option value="">-</option>
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
                            <label for="select-monitor" class="col-md-2 control-label">监视器</label>
                            <div class="col-md-10">
                                <select name="monitorId" id="select-monitor" class="form-control">
                                    <option value="">-</option>
                                    <c:forEach items="${monitors}" var="m">
                                        <c:choose>
                                            <c:when test="${m.id == entity.monitor.id}">
                                                <option value="${m.id}" selected="selected">${m.name}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${m.id}">${m.name}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="input-result-source" class="col-md-2 control-label">监视器结果源</label>
                            <div class="col-md-10">
                                <input type="text" name="resultSource" id="input-result-source" class="form-control" value="${entity.resultSource}" />
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="select-result-handler" class="col-md-2 control-label">监视器结果接收器</label>
                            <div class="col-md-10">
                                <select name="resultHandler" id="select-result-handler" class="form-control">
                                    <option value="">-</option>
                                    <c:forEach items="${resultHandlers}" var="rh">
                                        <c:choose>
                                            <c:when test="${rh == entity.resultHandler}">
                                                <option value="${rh}" selected="selected">${rh}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${rh}">${rh}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="input-msg-source" class="col-md-2 control-label">监视器推送源</label>
                            <div class="col-md-10">
                                <input type="text" name="messageSource" id="input-msg-source" class="form-control" value="${entity.messageSource}" />
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="select-msg-handler" class="col-md-2 control-label">监视器推送接收器</label>
                            <div class="col-md-10">
                                <select name="messageHandler" id="select-msg-handler" class="form-control">
                                    <option value="">-</option>
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
                    <c:if test="${mode == 'create'}">
                        <button type="button" id="btn-save" class="btn btn-primary">创建单元块</button>
                    </c:if>
                    <c:if test="${mode == 'modify'}">
                        <button type="button" id="btn-save" class="btn btn-primary">修改单元块</button>
                    </c:if>
                    <a class="btn btn-default" href="/admin/dashboards/${parent.id}/blocks">返回</a>
                </div>
            </div>
        </div>
        <script type="text/javascript">
$('#btn-save').on('click', function() {
    $('#form-block').submit();
});
setTimeout(function() {
    $('.flash-message').fadeOut();
}, 3000);
        </script>
    </body>
</html>
