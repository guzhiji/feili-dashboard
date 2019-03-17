var COLOR_ORDER = ['#963c3e','#3b5579', '#4e9845', '#ab9f52', '#729e8a','#749f83', '#ca8622', '#bda29a','#6e7074', '#546570', '#c4ccd3'],
    COLOR_TEXT = '#a5c8e6', COLOR_DARK_LINE = '#395273';

dashboard.registerDataRenderer('time-chart', function (blk) {
    var chart = TimeChart('block-' + blk.id + '-container', {
        maxLen: 100,
        xLabelFormatter: null,
        yLabelFormatter: null,
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
        fontSize: 10,
        theme: {
            colors: COLOR_ORDER,
            textColor: COLOR_TEXT
        }
    });
    chart.init(dashboard.utils.createFieldTransMap(blk.fields));
    return chart;
});

dashboard.registerDataRenderer('data-table', function(blk) {

});

dashboard.registerDataHandler('obj', function (renderer, blk, data) {
    switch (data.cmd) {
        case 'load':
            renderer.load(dashboard.utils.formatAllFields(blk.fields, data.data));
            break;
        case 'update':
            renderer.update(
                data.key,
                dashboard.utils.formatAllFields(blk.fields, data.data));
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
                return dashboard.utils.formatAllFields(blk.fields, obj);
            }));
            break;
        case 'update':
            renderer.update(
                data.key,
                dashboard.utils.formatAllFields(blk.fields, data.data));
            break;
        case 'remove':
            renderer.remove(data.key);
            break;
    }
});

dashboard.registerDataHandler('time-obj-list', function (renderer, blk, data) {
    switch (data.cmd) {
        case 'load':
            renderer.load(data.data.map(function (obj) {
                return {
                    key: parseInt(obj.time),
                    data: dashboard.utils.formatAllFields(blk.fields, obj.data)
                };
            }));
            break;
        case 'update':
            renderer.update(
                parseInt(data.key),
                dashboard.utils.formatAllFields(blk.fields, data.data));
            break;
    }
});
