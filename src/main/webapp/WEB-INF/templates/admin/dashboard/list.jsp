<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="content-type" content="text/html;charset=utf-8">
        <title>看板</title>
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
                    看板
                </div>
                <div class="panel-body">

                    <c:if test="${flashMessage == 'dashboard-saved'}">
                        <div class="alert alert-success flash-message">看板已经保存</div>
                    </c:if>
                    <c:if test="${flashMessage == 'dashboard-deleted'}">
                        <div class="alert alert-success flash-message">看板已经删除</div>
                    </c:if>
                    <c:if test="${flashMessage == 'dashboard-activated'}">
                        <div class="alert alert-success flash-message">看板已经开启</div>
                    </c:if>
                    <c:if test="${flashMessage == 'dashboard-deactivated'}">
                        <div class="alert alert-success flash-message">看板已经关闭</div>
                    </c:if>
                    <c:if test="${flashMessage == 'dashboard-not-activated'}">
                        <div class="alert alert-danger flash-message">看板开启失败</div>
                    </c:if>

                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>看板名称</th>
                                <th>看板路径</th>
                                <th></th>
                            </tr>
                        </head>
                        <tbody>
                            <c:forEach items="${list}" var="board">
                            <tr>
                                <td>${board.name}</td>
                                <td><a href="/dashboard/${board.pathKey}" target="_blank">${board.pathKey}</a></td>
                                <td class="row-actions">
                                    <a class="btn btn-default" href="/admin/dashboards/${board.id}/blocks">
                                        <span class="glyphicon glyphicon-eye-open"></span>
                                    </a>
                                    <a class="btn btn-primary" href="/admin/dashboards/${board.id}">
                                        <span class="glyphicon glyphicon-pencil"></span>
                                    </a>
                                    <c:if test="${board.active}">
                                        <a class="btn btn-info action-btn" data-action-url="/admin/dashboards/${board.id}/deactivate"
                                            data-action-desc="关闭看板：${board.name}" data-action-name="关闭"
                                            data-action-msg="确认要关闭该看板吗？">
                                            <span class="glyphicon glyphicon-stop"></span>
                                        </a>
                                    </c:if>
                                    <c:if test="${not board.active}">
                                        <a class="btn btn-info action-btn" data-action-url="/admin/dashboards/${board.id}/activate"
                                            data-action-desc="开启看板：${board.name}" data-action-name="开启"
                                            data-action-msg="确认要开启该看板吗？">
                                            <span class="glyphicon glyphicon-play"></span>
                                        </a>
                                    </c:if>
                                    <a class="btn btn-danger action-btn" data-action-url="/admin/dashboards/${board.id}/delete"
                                        data-action-desc="删除看板：${board.name}" data-action-name="删除"
                                        data-action-msg="确认要删除该看板吗？">
                                        <span class="glyphicon glyphicon-trash"></span>
                                    </a>
                                </td>
                            </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="panel-footer">
                    <a class="btn btn-primary" href="/admin/dashboards/new">创建看板</a>
                    <a class="btn btn-default" href="/admin/templates">管理模板</a>
                    <a class="btn btn-default" href="/admin/monitors">管理监视器</a>
                    <a class="btn btn-default" href="/admin/message-notifiers">管理数据推送源</a>
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
