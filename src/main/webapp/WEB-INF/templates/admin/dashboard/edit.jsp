<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="content-type" content="text/html;charset=utf-8">
        <title>
            <c:if test="${mode == 'create'}">
                看板：创建
            </c:if>
            <c:if test="${mode == 'modify'}">
                看板：${entity.name}
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
                        看板：创建
                    </c:if>
                    <c:if test="${mode == 'modify'}">
                        看板：${entity.name}（#${entity.id}）
                    </c:if>
                </div>
                <div class="panel-body">

                    <c:choose>
                        <c:when test="${flashMessage == 'dashboard-pathkey-empty'}">
                            <div class="alert alert-danger flash-message">请填写地址名称</div>
                        </c:when>
                        <c:when test="${flashMessage == 'dashboard-name-empty'}">
                            <div class="alert alert-danger flash-message">请填写名称</div>
                        </c:when>
                        <c:when test="${flashMessage == 'dashboard-tpl-empty'}">
                            <div class="alert alert-danger flash-message">请选择模板</div>
                        </c:when>
                    </c:choose>

                    <form class="form-horizontal" id="form-dashboard" method="POST"
                        action="${saveUrl}">

                        <div class="form-group">
                            <label for="input-name" class="col-md-2 control-label">名称</label>
                            <div class="col-md-10">
                                <input type="text" name="name" id="input-name" class="form-control" value="${entity.name}" />
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="input-path-key" class="col-md-2 control-label">地址名称</label>
                            <div class="col-md-10">
                                <input type="text" name="pathKey" id="input-path-key" class="form-control" value="${entity.pathKey}" />
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
                            <label for="select-template" class="col-md-2 control-label">模板</label>
                            <div class="col-md-10">
                                <select name="templateId" id="select-template" class="form-control">
                                    <option value="">-</option>
                                    <c:forEach items="${templates}" var="tpl">
                                        <c:choose>
                                            <c:when test="${tpl.id == entity.template.id}">
                                                <option value="${tpl.id}" selected="selected">${tpl.name}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${tpl.id}">${tpl.name}</option>
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
                        <button type="button" id="btn-save" class="btn btn-primary">创建看板</button>
                    </c:if>
                    <c:if test="${mode == 'modify'}">
                        <button type="button" id="btn-save" class="btn btn-primary">修改看板</button>
                    </c:if>
                    <a class="btn btn-default" href="/admin/dashboards">返回</a>
                </div>
            </div>
        </div>
        <script type="text/javascript">
$('#btn-save').on('click', function() {
    $('#form-dashboard').submit();
});
setTimeout(function() {
    $('.flash-message').fadeOut();
}, 3000);
        </script>
    </body>
</html>

