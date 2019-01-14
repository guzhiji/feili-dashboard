<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="content-type" content="text/html;charset=utf-8">
        <title>
            <c:if test="${mode == 'create'}">
                数据推送源：添加
            </c:if>
            <c:if test="${mode == 'modify'}">
                数据推送源：${entity.name}
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
                        数据推送源：添加
                    </c:if>
                    <c:if test="${mode == 'modify'}">
                        数据推送源：${entity.name}（#${entity.id}）
                    </c:if>
                </div>
                <div class="panel-body">

                    <c:choose>
                        <c:when test="${flashMessage == 'notifier-name-empty'}">
                            <div class="alert alert-danger flash-message">请填写数据推送源名称</div>
                        </c:when>
                        <c:when test="${flashMessage == 'notifier-javaclass-empty'}">
                            <div class="alert alert-danger flash-message">请填写数据推送源Java类</div>
                        </c:when>
                        <c:when test="${flashMessage == 'notifier-javaclass-invalid'}">
                            <div class="alert alert-danger flash-message">数据推送源Java类不合法</div>
                        </c:when>
                    </c:choose>

                    <form class="form-horizontal" id="form-message-notifier" method="POST"
                        action="${saveUrl}">

                        <div class="form-group">
                            <label for="input-name" class="col-md-2 control-label">名称</label>
                            <div class="col-md-10">
                                <input type="text" name="name" id="input-name" class="form-control"
                                    value="${entity.name}" />
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="input-java-class" class="col-md-2 control-label">Java类</label>
                            <div class="col-md-10">
                                <input type="text" name="javaClass" id="input-java-class" class="form-control"
                                    value="${entity.javaClass}" />
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="col-md-offset-2 col-md-10">
                                <div class="checkbox">
                                    <label>
                                        <c:if test="${entity.monitor}">
                                            <input type="checkbox" name="monitor" checked="checked" />
                                        </c:if>
                                        <c:if test="${not entity.monitor}">
                                            <input type="checkbox" name="monitor" />
                                        </c:if>
                                        是否为监视器
                                    </label>
                                </div>
                            </div>
                        </div>

                    </form>
                </div>
                <div class="panel-footer">
                    <c:if test="${mode == 'create'}">
                        <button type="button" id="btn-save" class="btn btn-primary">添加数据推送源</button>
                    </c:if>
                    <c:if test="${mode == 'modify'}">
                        <button type="button" id="btn-save" class="btn btn-primary">修改数据推送源</button>
                    </c:if>
                    <a class="btn btn-default" href="/admin/message-notifiers">返回</a>
                </div>
            </div>
        </div>
        <script type="text/javascript">
$('#btn-save').on('click', function() {
    $('#form-message-notifier').submit();
});
setTimeout(function() {
    $('.flash-message').fadeOut();
}, 3000);
        </script>
    </body>
</html>


