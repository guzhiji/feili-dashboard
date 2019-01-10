<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="content-type" content="text/html;charset=utf-8">
        <title>
            <c:if test="${mode == 'create'}">
                模板：创建
            </c:if>
            <c:if test="${mode == 'modify'}">
                模板：${entity.name}
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
                        模板：创建
                    </c:if>
                    <c:if test="${mode == 'modify'}">
                        模板：${entity.name}（#${entity.id}）
                    </c:if>
                </div>
                <div class="panel-body">

                    <c:choose>
                        <c:when test="${flashMessage == 'template-name-empty'}">
                            <div class="alert alert-danger flash-message">请填写名称</div>
                        </c:when>
                        <c:when test="${flashMessage == 'template-internalname-empty'}">
                            <div class="alert alert-danger flash-message">请填写内部名称</div>
                        </c:when>
                    </c:choose>

                    <form class="form-horizontal" id="form-template" method="POST"
                        action="${saveUrl}">

                        <div class="form-group">
                            <label for="input-name" class="col-md-2 control-label">名称</label>
                            <div class="col-md-10">
                                <input type="text" name="name" id="input-name" class="form-control"
                                    value="${entity.name}" />
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="input-internal-name" class="col-md-2 control-label">内部名称</label>
                            <div class="col-md-10">
                                <input type="text" name="internalName" id="input-internal-name" class="form-control"
                                    value="${entity.internalName}" />
                            </div>
                        </div>

                    </form>
                </div>
                <div class="panel-footer">
                    <c:if test="${mode == 'create'}">
                        <button type="button" id="btn-save" class="btn btn-primary">创建模板</button>
                    </c:if>
                    <c:if test="${mode == 'modify'}">
                        <button type="button" id="btn-save" class="btn btn-primary">修改模板</button>
                    </c:if>
                    <a class="btn btn-default" href="/admin/templates">返回</a>
                </div>
            </div>
        </div>
        <script type="text/javascript">
$('#btn-save').on('click', function() {
    $('#form-template').submit();
});
setTimeout(function() {
    $('.flash-message').fadeOut();
}, 3000);
        </script>
    </body>
</html>
