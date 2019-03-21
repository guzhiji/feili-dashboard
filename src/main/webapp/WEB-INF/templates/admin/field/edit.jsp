<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="content-type" content="text/html;charset=utf-8">
        <title>
            <c:if test="${mode == 'create'}">
                字段：创建
            </c:if>
            <c:if test="${mode == 'modify'}">
                字段：${entity.name}
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
                        字段：创建
                    </c:if>
                    <c:if test="${mode == 'modify'}">
                        字段：${entity.name}（#${entity.id}）
                    </c:if>
                </div>
                <div class="panel-body">

                    <c:choose>
                        <c:when test="${flashMessage == 'field-name-empty'}">
                            <div class="alert alert-danger flash-message">请填写名称</div>
                        </c:when>
                        <c:when test="${flashMessage == 'field-internalname-empty'}">
                            <div class="alert alert-danger flash-message">请填写内部名称</div>
                        </c:when>
                    </c:choose>

                    <form class="form-horizontal" id="form-field" method="POST"
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

                        <div class="form-group">
                            <label for="input-value-transformer" class="col-md-2 control-label">数值转换</label>
                            <div class="col-md-10">
                                <input type="text" name="valueTransformer" id="input-value-transformer" class="form-control"
                                    value="${entity.valueTransformer}" />
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="input-value-formatter" class="col-md-2 control-label">数值格式</label>
                            <div class="col-md-10">
                                <input type="text" name="valueFormatter" id="input-value-formatter" class="form-control"
                                    value="${entity.valueFormatter}" />
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

                    </form>
                </div>
                <div class="panel-footer">
                    <c:if test="${mode == 'create'}">
                        <button type="button" id="btn-save" class="btn btn-primary">创建字段</button>
                    </c:if>
                    <c:if test="${mode == 'modify'}">
                        <button type="button" id="btn-save" class="btn btn-primary">修改字段</button>
                    </c:if>
                    <a class="btn btn-default" href="/admin/blocks/${parent.id}/fields">返回</a>
                </div>
            </div>
        </div>
        <script type="text/javascript">
$('#btn-save').on('click', function() {
    $('#form-field').submit();
});
setTimeout(function() {
    $('.flash-message').fadeOut();
}, 3000);
        </script>
    </body>
</html>
