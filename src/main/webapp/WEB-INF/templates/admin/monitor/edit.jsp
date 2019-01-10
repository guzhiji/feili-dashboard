<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="content-type" content="text/html;charset=utf-8">
        <title>
            <c:if test="${mode == 'create'}">
                监视器：创建
            </c:if>
            <c:if test="${mode == 'modify'}">
                监视器：${entity.name}
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
                        监视器：创建
                    </c:if>
                    <c:if test="${mode == 'modify'}">
                        监视器：${entity.name}（#${entity.id}）
                    </c:if>
                </div>
                <div class="panel-body">

                    <c:choose>
                        <c:when test="${flashMessage == 'monitor-name-empty'}">
                            <div class="alert alert-danger flash-message">请填写监视器名称</div>
                        </c:when>
                        <c:when test="${flashMessage == 'monitor-javaclass-empty'}">
                            <div class="alert alert-danger flash-message">请填写监视器Java类</div>
                        </c:when>
                        <c:when test="${flashMessage == 'monitor-javaclass-invalid'}">
                            <div class="alert alert-danger flash-message">监视器Java类不合法</div>
                        </c:when>
                        <c:when test="${flashMessage == 'monitor-execrate-empty'}">
                            <div class="alert alert-danger flash-message">请填写监视器执行频率</div>
                        </c:when>
                    </c:choose>

                    <form class="form-horizontal" id="form-monitor" method="POST"
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
                            <label for="input-args" class="col-md-2 control-label">参数</label>
                            <div class="col-md-10">
                                <input type="text" name="args" id="input-args" class="form-control"
                                    value="${entity.args}" />
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="input-exec-rate" class="col-md-2 control-label">执行频率</label>
                            <div class="col-md-10">
                                <input type="text" name="execRate" id="input-exec-rate" class="form-control"
                                    value="${entity.execRate}" />
                            </div>
                        </div>

                    </form>
                </div>
                <div class="panel-footer">
                    <c:if test="${mode == 'create'}">
                        <button type="button" id="btn-save" class="btn btn-primary">创建监视器</button>
                    </c:if>
                    <c:if test="${mode == 'modify'}">
                        <button type="button" id="btn-save" class="btn btn-primary">修改监视器</button>
                    </c:if>
                    <a class="btn btn-default" href="/admin/monitors">返回</a>
                </div>
            </div>
        </div>
        <script type="text/javascript">
$('#btn-save').on('click', function() {
    $('#form-monitor').submit();
});
setTimeout(function() {
    $('.flash-message').fadeOut();
}, 3000);
        </script>
    </body>
</html>

