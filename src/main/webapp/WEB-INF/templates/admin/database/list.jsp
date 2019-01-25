<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="content-type" content="text/html;charset=utf-8">
        <title>数据库连接</title>
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
                    数据库连接
                </div>
                <div class="panel-body">

                    <c:if test="${flashMessage == 'database-saved'}">
                        <div class="alert alert-success flash-message">数据库连接已经保存</div>
                    </c:if>
                    <c:if test="${flashMessage == 'database-deleted'}">
                        <div class="alert alert-success flash-message">数据库连接已经删除</div>
                    </c:if>

                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>数据库连接名称</th>
                                <th>驱动器</th>
                                <th>连接串</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${list}" var="db">
                            <tr>
                                <td>${db.name}</td>
                                <td>${db.dbDriver}</td>
                                <td>${db.dbUri}</td>
                                <td class="row-actions">
                                    <a class="btn btn-primary" href="/admin/databases/${db.id}">
                                        <span class="glyphicon glyphicon-pencil"></span>
                                    </a>
                                    <a class="btn btn-danger action-btn" data-action-url="/admin/databases/${db.id}/delete"
                                        data-action-desc="删除数据库连接：${db.name}" data-action-name="删除"
                                        data-action-msg="确认要删除该数据库连接吗？">
                                        <span class="glyphicon glyphicon-trash"></span>
                                    </a>
                                </td>
                            </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                </div>
                <div class="panel-footer">
                    <a class="btn btn-primary" href="/admin/databases/new">添加数据库连接</a>
                    <a class="btn btn-default" href="/admin/dashboards">返回</a>
                </div>
            </div>
        </div>
        <div class="modal fade" id="dialog-confirm" role="dialog">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">
                            <span>&times;</span>
                        </button>
                        <h4 class="modal-title"></h4>
                    </div>
                    <div class="modal-body">
                    </div>
                    <div class="modal-footer">
                        <form id="dialog-confirm-form" method="POST"></form>
                        <button type="button" class="btn btn-danger" id="dialog-confirm-btn"></button>
                        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                    </div>
                </div>
            </div>
        </div>
        <script type="text/javascript">
$('.action-btn').on('click', function() {
    var self = $(this),
        name = self.data('action-name'),
        desc = self.data('action-desc'),
        url = self.data('action-url'),
        msg = self.data('action-msg'),
        dialog = $('#dialog-confirm');
    dialog.find('.modal-title').text(desc);
    dialog.find('.modal-body').text(msg);
    dialog.find('#dialog-confirm-form').attr('action', url);
    dialog.find('#dialog-confirm-btn').text(name)
        .off('click')
        .on('click', function() {
            dialog.find('#dialog-confirm-form').submit();
        });
    dialog.modal();
});
setTimeout(function() {
    $('.flash-message').fadeOut();
}, 3000);
        </script>
    </body>
</html>

