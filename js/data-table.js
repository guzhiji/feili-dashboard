
function DataTable(id, $, config) {
    var table = $('#' + id),
        thead = null,
        page = 0,
        data = [];

    thead = table.find('>thead');
    if (thead.length == 0) {
        thead = $('<head></head>');
        table.append(thead);
    }

    for (var f in config.fields) {

    }
}
