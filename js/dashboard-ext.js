(function($) {

var COLOR_ORDER = ['#963c3e','#3b5579', '#4e9845', '#ab9f52', '#729e8a','#749f83', '#ca8622', '#bda29a','#6e7074', '#546570', '#c4ccd3'],
    COLOR_TEXT = '#a5c8e6', COLOR_DARK_LINE = '#395273';

dashboard.registerDataRenderer('time-chart', function (blk) {
    var chart = TimeChart('block-' + blk.id + '-container', {
        maxLen: 100,
        // xLabelFormatter: null,
        yLabelFormatter: function(v) {
            if (blk.fields.length > 0) {
                return dashboard.utils.formatValue(
                    blk.fields[0].valueFormatter, v);
            }
            return v;
        },
        showPoints: true,
        fontSize: 10,
        theme: {
            colors: COLOR_ORDER,
            textColor: COLOR_TEXT,
            lineColor: COLOR_DARK_LINE
        }
    });
    chart.init(dashboard.utils.createFieldTransMap(blk.fields));
    return chart;
});

dashboard.registerDataRenderer('pie-chart', function(blk) {
    var chart = PieChart('block-' + blk.id + '-container', {
        valueLabelFormatter: function(v) {
            if (blk.fields.length > 0) {
                return dashboard.utils.formatValue(
                    blk.fields[0].valueFormatter, v);
            }
            return v;
        },
        fontSize: 10,
        theme: {
            colors: COLOR_ORDER,
            textColor: COLOR_TEXT
        }
    });
    chart.init(dashboard.utils.createFieldTransMap(blk.fields));
    return chart;
});

dashboard.registerDataRenderer('category-chart', function(blk) {
    var chart = CategoryChart('block-' + blk.id + '-container', {
        type: 'bar',
        vertical: false,
        valueLabelFormatter: function(v) {
            if (blk.fields.length > 0) {
                return dashboard.utils.formatValue(
                    blk.fields[0].valueFormatter, v);
            }
            return v;
        },
        fontSize: 10,
        theme: {
            colors: COLOR_ORDER,
            textColor: COLOR_TEXT,
            lineColor: COLOR_DARK_LINE
        }
    });
    chart.init(dashboard.utils.createFieldTransMap(blk.fields));
    return chart;
});

dashboard.registerDataRenderer('data-table', function(blk) {
    var container = $('#block-' + blk.id + '-container'),
        table = container.find('table');
    if (table.length == 0) {
        table = $('<table class="table table-striped"></table>');
        container.append(table);
    }
    if (!table.hasClass('table')) table.addClass('table');
    if (!table.hasClass('table-striped')) table.addClass('table-striped');
    return DataTable(table, $, {
        fields: blk.fields.map(function(f) {
            return {
                // key: f.internalName,
                key: f.id,
                name: f.name,
                formatter: function(value) {
                    return dashboard.utils.formatValue(f.valueFormatter, value);
                }
            };
        }),
        autoGrow: false,
        maxRows: 5,
        minRows: 5,
        bottomSpace: 0,
        pageSpeed: 3000
    });
});

dashboard.registerDataHandler('obj', function (renderer, blk, data) {
    switch (data.cmd) {
        case 'load':
            renderer.load(dashboard.utils.transformAllFields(blk.fields, data.data));
            break;
        case 'update':
            renderer.update(
                data.key,
                dashboard.utils.transformAllFields(blk.fields, data.data));
            break;
        case 'remove':
            renderer.remove(data.key);
            break;
    }
});

dashboard.registerDataHandler('obj-list', function (renderer, blk, data) {
    switch (data.cmd) {
        case 'load':
            renderer.load(data.data.map(function (obj) {
                if ('key' in obj && 'data' in obj && typeof(obj.data) == 'object')
                    return {
                        key: obj.key,
                        data: dashboard.utils.transformAllFields(blk.fields, obj.data)
                    };
                return dashboard.utils.transformAllFields(blk.fields, obj);
            }));
            break;
        case 'update':
            renderer.update(
                data.key,
                dashboard.utils.transformAllFields(blk.fields, data.data));
            break;
        case 'remove':
            renderer.remove(data.key);
            break;
    }
});

})(jQuery);