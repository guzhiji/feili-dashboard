<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="content-type" content="text/html;charset=utf-8">
        <title>
            <c:if test="${mode == 'create'}">
                数据库连接：添加
            </c:if>
            <c:if test="${mode == 'modify'}">
                数据库连接：${entity.name}
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
                        数据库连接：添加
                    </c:if>
                    <c:if test="${mode == 'modify'}">
                        数据库连接：${entity.name}（#${entity.id}）
                    </c:if>
                </div>
                <div class="panel-body">

                    <c:choose>
                        <c:when test="${flashMessage == 'database-name-empty'}">
                            <div class="alert alert-danger flash-message">请填写数据库连接名称</div>
                        </c:when>
                        <c:when test="${flashMessage == 'database-name-toolong'}">
                            <div class="alert alert-danger flash-message">数据库连接名称过长，请在32字符以内</div>
                        </c:when>
                        <c:when test="${flashMessage == 'database-driver-empty'}">
                            <div class="alert alert-danger flash-message">请填写数据库驱动器</div>
                        </c:when>
                        <c:when test="${flashMessage == 'database-uri-empty'}">
                            <div class="alert alert-danger flash-message">请填写数据库连接串</div>
                        </c:when>
                        <c:when test="${flashMessage == 'database-user-empty'}">
                            <div class="alert alert-danger flash-message">请填写数据库连接用户名</div>
                        </c:when>
                    </c:choose>

                    <form class="form-horizontal" id="form-database" method="POST"
                        action="${saveUrl}">

                        <div class="form-group">
                            <label for="input-name" class="col-md-2 control-label">名称</label>
                            <div class="col-md-10">
                                <input type="text" name="name" id="input-name" class="form-control"
                                    value="${entity.name}" />
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="input-db-driver" class="col-md-2 control-label">驱动器</label>
                            <div class="col-md-10">
                                <input type="text" name="dbDriver" id="input-db-driver" class="form-control"
                                    value="${entity.dbDriver}" />
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="input-db-uri" class="col-md-2 control-label">连接串</label>
                            <div class="col-md-10">
                                <input type="text" name="dbUri" id="input-db-uri" class="form-control"
                                    value="${entity.dbUri}" />
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="input-db-user" class="col-md-2 control-label">用户名</label>
                            <div class="col-md-10">
                                <input type="text" name="dbUser" id="input-db-user" class="form-control"
                                    value="${entity.dbUser}" />
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="input-db-pass" class="col-md-2 control-label">密码</label>
                            <div class="col-md-10">
                                <input type="password" name="dbPass" id="input-db-pass" class="form-control"
                                    value="${entity.dbPass}" />
                            </div>
                        </div>

                    </form>
                </div>
                <div class="panel-footer">
                    <c:if test="${mode == 'create'}">
                        <button type="button" id="btn-save" class="btn btn-primary">添加数据库连接</button>
                    </c:if>
                    <c:if test="${mode == 'modify'}">
                        <button type="button" id="btn-save" class="btn btn-primary">修改数据库连接</button>
                    </c:if>
                    <a class="btn btn-default" href="/admin/databases">返回</a>
                </div>
            </div>
        </div>
        <script type="text/javascript">
$('#btn-save').on('click', function() {
    $('#form-database').submit();
});
setTimeout(function() {
    $('.flash-message').fadeOut();
}, 3000);
        </script>
    </body>
</html>

