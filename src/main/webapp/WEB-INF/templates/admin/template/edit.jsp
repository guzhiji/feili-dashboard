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
                    模板
                </div>
                <div class="panel-body">
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
                    <button type="button" id="btn-save" class="btn btn-primary">添加模板</button>
                    <a class="btn btn-default" href="/admin/templates">返回</a>
                </div>
            </div>
        </div>
        <script type="text/javascript">
$('#btn-save').on('click', function() {
    $('#form-template').submit();
});
        </script>
    </body>
</html>
