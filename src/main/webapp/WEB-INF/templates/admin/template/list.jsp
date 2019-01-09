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
        <style>
            table.table tbody td.row-actions {
                text-align: right;
                width: 200px;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    模板
                </div>
                <div class="panel-body">
            
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>模板名称</th>
                                <th>内部名称</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${list}" var="tpl">
                            <tr>
                                <td>${tpl.name}</td>
                                <td>${tpl.internalName}</td>
                                <td class="row-actions">
                                    <a class="btn btn-primary" href="/admin/templates/${tpl.id}">
                                        <span class="glyphicon glyphicon-pencil"></span>
                                    </a>
                                </td>
                            </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                </div>
                <div class="panel-footer">
                    <a class="btn btn-primary" href="/admin/templates/new">添加模板</a>
                    <a class="btn btn-default" href="/admin/dashboards">返回</a>
                </div>
            </div>
        </div>
    </body>
</html>

