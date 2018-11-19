var LINECHART_MAX = 24,
    COLOR_ORDER = ['#963c3e','#3b5579', '#4e9845', '#ab9f52', '#729e8a','#749f83', '#ca8622', '#bda29a','#6e7074', '#546570', '#c4ccd3'],
    COLOR_TEXT = '#a5c8e6', COLOR_DARK_LINE = '#395273';

function calcRemainingTime(t) {
    if (typeof t != 'number' && !t)
        return Infinity;
    return new Date(t) - new Date();
}

function formatDuration(ms) {
    var l = Math.round(ms / 1000), t, r;
    if (l < 60)
        return l + '秒';
    l /= 60;
    if (l < 60) {
        t = Math.floor(l);
        r = t + '分';
        if (l > t) r += formatDuration((l - t) * 60000);
        return r;
    }
    l /= 60;
    t = Math.floor(l);
    r = t + '时';
    if (l > t) r += formatDuration((l - t) * 3600000);
    return r;
}

function formatDuration2(ms) {
    if (typeof ms == 'number' && ms != Infinity)
        return ms < 0 ? '-' + formatDuration(-ms) : formatDuration(ms);
    return '-';
}

function formatNameAndKey(name, key) {
    var result = name || '';
    if (key) result = result ? result + '(' + key + ')' : key;
    return result || '-';
}

function deserializeMessage(str, valueFormatter) {
    var rows = str.split(';'),
        data = {},
        entry, r;
    for (r = 0; r < rows.length; r++) {
        if (!rows[r] || rows[r].indexOf('=') == -1) continue;
        entry = rows[r].split('=');
        try {
            data[entry[0]] = valueFormatter(entry[1]);
        } catch (e) {}
    }
    return data;
}

var PieChart = function(id, name, translatedLabels) {
    var chart = echarts.init(document.getElementById(id)),
        legends = [],
        data = [],
        option = {
            color: COLOR_ORDER,
            legend: {
                textStyle: {
                    color: COLOR_TEXT
                },
                data: legends
            },
            series: [
                {
                    name: name,
                    type: 'pie',
                    radius: '50%',
                    label: {
                        show: true,
                        color: COLOR_TEXT,
                        formatter: '{b}: {c} - {d}%'
                    },
                    itemStyle: {
                        opacity: 0.9
                    },
                    data: data
                }
            ]
        };

    chart.setOption(option);

    return {
        clear: function() {
            for (var i = 0; i < data.length; i++)
                data[i].value = 0;
            chart.setOption({
                series: [{data: data}]
            });
        },
        update: function(values) {
            for (var key in values) {
                var label = key in translatedLabels ? translatedLabels[key] : key,
                    which = legends.indexOf(label);
                if (which == -1) {
                    legends.push(label);
                    data.push({
                        name: label,
                        value: values[key]
                    });
                    chart.setOption({
                        legend: {data: legends},
                        series: [{data: data}]
                    });
                } else {
                    data[which].value = values[key];
                    chart.setOption({
                        series: [{data: data}]
                    });
                }
            }
        }
    };
};

var LineChart = function(id, formatter, translatedLabels) {
    var chart = echarts.init(document.getElementById(id)),
        legends = [],
        data = [],
        option = {
            color: COLOR_ORDER,
            animation: false,
            legend: {
                textStyle: {
                    color: COLOR_TEXT
                },
                data: legends
            },
            tooltip: {
                trigger: 'axis',
                formatter: function(params) {
                    var t = '';
                    if (typeof(formatter) == 'function') {
                        for (var i = 0; i < params.length; i++) {
                            t += params[i].seriesName + ': ' +
                                formatter(params[i].value[1]) + '<br>\n';
                        }
                    } else {
                        for (var i = 0; i < params.length; i++) {
                            t += params[i].seriesName + ': ' +
                                params[i].value[1] + '<br>\n';
                        }
                    }
                    return t;
                }
            },
            xAxis: [
                {
                    type: 'time',
                    showMinLabel: false,
                    showMaxLabel: false,
                    splitLine: {
                        lineStyle: {
                            color: [COLOR_DARK_LINE]
                        }
                    },
                    axisLine: {
                        lineStyle: {
                            color: COLOR_DARK_LINE
                        }
                    },
                    axisLabel: {
                        color: COLOR_TEXT
                    }
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    interval: 1,
                    splitLine: {
                        lineStyle: {
                            color: [COLOR_DARK_LINE]
                        }
                    },
                    axisLine: {
                        lineStyle: {
                            color: COLOR_DARK_LINE
                        }
                    },
                    axisLabel: {
                        color: COLOR_TEXT,
                        formatter: formatter
                    }
                }
            ],
            serise: data
        };

    function createSerie(name) {
        return {
            name: name,
            type: 'line',
            symbolSize: 8,
            itemStyle: { normal: { areaStyle: { type: 'default' } } },
            data: []
        };
    }

    chart.setOption(option);

    return {
        clear: function() {
            data = [];
            chart.setOption({
                series: data
            });
        },
        update: function(t, value) {
            for (var key in value) {
                var legend = key in translatedLabels ? translatedLabels[key] : key,
                    p = legends.indexOf(legend);
                if (p == -1) {
                    legends.push(legend);
                    var s = createSerie(legend);
                    s.data.push([
                        new Date(t),
                        value[key]
                    ]);
                    data.push(s);
                    chart.setOption({
                        legend: {data: legends},
                        series: data
                    });
                } else {
                    var l = data[p].data.length, matchesPrev = false;
                    if (l > 0) {
                        var d = data[p].data[l - 1]; // previous data point
                        if (d[0].getTime() == t) {
                            d[1] = value[key];
                            matchesPrev = true;
                        }
                    }
                    if (!matchesPrev) {
                        if (l >= LINECHART_MAX)
                            data[p].data.shift();
                        data[p].data.push([
                            new Date(t),
                            value[key]
                        ]);
                    }
                    chart.setOption({
                        series: data
                    });
                }
            }
        },
        load: function(values) {
            data = [];
            for (var i = 0; i < values.length; i++) {
                for (var key in values[i].data) {
                    var legend = key in translatedLabels ? translatedLabels[key] : key,
                        p = legends.indexOf(legend);
                    if (p == -1) {
                        legends.push(legend);
                        var s = createSerie(legend);
                        s.data.push([
                            new Date(values[i].hour),
                            values[i].data[key]
                        ]);
                        data.push(s);
                    } else {
                        if (data[p].data.length >= LINECHART_MAX)
                            data[p].data.shift();
                        data[p].data.push([
                            new Date(values[i].hour),
                            values[i].data[key]
                        ]);
                    }
                }
            }
            chart.setOption({
                legend: {data: legends},
                series: data
            });
        }
    };
};

var DataTable = function(id, refreshRate, fields) {
    var table = $('#' + id),
        page = 0,
        data = [];

    function createRow(body) {
        var r = $('<tr></tr>'),
            i;
        for (i = 0; i < fields.length; i++)
            r.append('<td>&nbsp;</td>');
        body.append(r);
    }

    function initRows() {
        var tbody = table.find('tbody'),
            windowHeight = $(window).height(),
            tableOffset = table.offset(),
            count = 0;
        tbody.empty();
        while (table.outerHeight() + tableOffset.top + 150 < windowHeight) {
            createRow(tbody);
            count++;
        }
        while (count < 5) {
            createRow(tbody);
            count++;
        }
    }

    function renderData() {
        // page count
        var rows = table.find('tbody > tr'),
            pageCount = data.length / rows.length;
        if (Math.floor(pageCount) < pageCount)
            pageCount = Math.floor(pageCount) + 1;
        // next page
        page++;
        if (page > pageCount) page = 1;
        // starting position
        var s = (page - 1) * rows.length;
        rows.each(function(r) {
            var row = $(this);
            if (s + r < data.length) {
                if (pageCount > 1) {
                    row.hide('fast', function() {
                        row.find('td').each(function(c) {
                            $(this).text(fields[c](data[s + r]));
                        });
                    });
                    row.show('fast');
                } else {
                    row.find('td').each(function(c) {
                        $(this).text(fields[c](data[s + r]));
                    });
                }
            } else {
                row.find('td').each(function() {
                    $(this).html('&nbsp;');
                });
            }
        });
    }

    $(window).on('resize', function() {
        initRows();
        renderData();
    });

    initRows();

    setInterval(renderData, refreshRate);
    renderData();

    return {
        clear: function() {
            data = [];
            renderData();
        },
        render: renderData,
        update: function(values) {
            for (var i = 0; i < values.length; i++)
                values[i].timeRemaining = calcRemainingTime(values[i].shipDate);
            values.sort(function(a, b) {
                if (a.toCombine && !b.toCombine) return -1;
                if (!a.toCombine && b.toCombine) return 1;
                if (a.timeRemaining < b.timeRemaining) return -1;
                if (a.timeRemaining > b.timeRemaining) return 1;
                if (a.trolleyId < b.trolleyId) return -1;
                if (a.trolleyId > b.trolleyId) return 1;
                if (a.orderKey < b.orderKey) return -1;
                if (a.orderKey > b.orderKey) return 1;
                return 0;
            });
            data = values;
        }
    };
};
