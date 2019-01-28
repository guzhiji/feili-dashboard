var COLOR_ORDER = ['#963c3e','#3b5579', '#4e9845', '#ab9f52', '#729e8a','#749f83', '#ca8622', '#bda29a','#6e7074', '#546570', '#c4ccd3'],
    COLOR_TEXT = '#a5c8e6', COLOR_DARK_LINE = '#395273';

var timeDiff = 0;

function setBaseTime(t) {
    timeDiff = new Date(parseInt(t)) - new Date();
}

function now() {
    return new Date().getTime() + timeDiff;
}

function calcRemainingTime(t) {
    if (typeof t != 'number' && !t)
        return Infinity;
    return new Date(t).getTime() - now();
}

function uptoNow(value) {
    return now() - new Date(value).getTime();
}

function formatDuration(ms) {
    var l = Math.round((ms > 0 ? ms : 0) / 1000), t, r;
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

function estimateBaseFontSize() {
    var w = $(window),
        width = w.width(),
        f = $('h1'),
        fh = f.height();
    if (width >= 3800)
        return (fh / 3.5).toFixed(1);
    if (width >= 1900)
        return (fh / 2.5).toFixed(1);
    if (width >= 1200)
        return (fh / 1.5).toFixed(1);
    return fh;
}

function estimateChartFontSize() {
    var w = $(window),
        width = w.width(),
        base = estimateBaseFontSize();
    if (width >= 3800)
        return (base * 1.8).toFixed(0);
    if (width >= 1900)
        return (base * 1.2).toFixed(0);
    if (width >= 1200)
        return (base * 0.8).toFixed(0);
    return base * 0.5;
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
                    radius: '40%',
                    label: {
                        show: true,
                        color: COLOR_TEXT,
                        formatter: '{b}: {c} \n {d}%'
                    },
                    itemStyle: {
                        opacity: 0.9
                    },
                    data: data
                }
            ]
        };

    function update(values) {
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

    function updateFontSize(size) {
        option.legend.textStyle.fontSize = size;
        option.series[0].label.fontSize = size;
        chart.setOption({
            legend: {
                textStyle: {
                    fontSize: size
                }
            },
            series: [
                {
                    label: {
                        fontSize: size
                    }
                }
            ]
        });
    }

    function updateFontColor(color) {
        option.legend.textStyle.color = color;
        option.series[0].label.color = color;
        chart.setOption({
            legend: {
                textStyle: {
                    color: color
                }
            },
            series: [
                {
                    label: {
                        color: color
                    }
                }
            ]
        });
    }

    chart.setOption(option);
    (function() {
        var initialData = {};
        for (var key in translatedLabels)
            initialData[key] = 0;
        update(initialData);
    })();

    return {
        rebind: function(id) {
            chart.dispose();
            chart = echarts.init(document.getElementById(id));
            chart.setOption(option);
        },
        render: function() {
            chart.setOption({
                legend: { data: legends },
                series: [{ data: data }]
            });
        },
        clear: function() {
            for (var i = 0; i < data.length; i++)
                data[i].value = 0;
            chart.setOption({
                series: [{data: data}]
            });
        },
        updateFontSize: updateFontSize,
        updateFontColor: updateFontColor,
        update: update
    };
};

var LineChart = function(id, max_len, formatter, xFormatter, showPoints, translatedLabels) {
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
                    minInterval: 1,
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

    function createSeries(name) {
        var series = {
            name: name,
            type: 'line',
            itemStyle: { normal: { areaStyle: { type: 'default' } } },
            data: []
        };
        if (showPoints) {
            series.symbol = 'circle';
            series.symbolSize = 8;
        } else {
            series.symbol = 'none';
        }
        return series;
    }

    function update(t, value) {
        for (var key in value) {
            var legend = key in translatedLabels ? translatedLabels[key] : key,
                p = legends.indexOf(legend);
            if (p == -1) {
                legends.push(legend);
                var s = createSeries(legend);
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
                    if (l >= max_len)
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
    }

    function initData() {
        legends = [];
        data = [];
        for (var key in translatedLabels) {
            var legend = translatedLabels[key];
            legends.push(legend);
            data.push(createSeries(legend));
        }
    }

    function renderData() {
        chart.setOption({
            legend: {data: legends},
            series: data
        });
    }

    function updateFontSize(size) {
        option.legend.textStyle.fontSize = size;
        if (option.tooltip.textStyle)
            option.tooltip.textStyle.fontSize = size;
        else
            option.tooltip.textStyle = {fontSize: size};
        option.xAxis[0].axisLabel.fontSize = size;
        option.yAxis[0].axisLabel.fontSize = size;
        chart.setOption({
            legend: {
                textStyle: {
                    fontSize: size
                }
            },
            tooltip: {
                textStyle: {
                    fontSize: size
                }
            },
            xAxis: [
                {
                    axisLabel: {
                        fontSize: size
                    }
                }
            ],
            yAxis: [
                {
                    axisLabel: {
                        fontSize: size
                    }
                }
            ]
        });
    }

    function updateFontColor(color) {
        option.legend.textStyle.color = color;
        if (option.tooltip.textStyle)
            option.tooltip.textStyle.color = color;
        else
            option.tooltip.textStyle = {color: color};
        option.xAxis[0].axisLabel.color = color;
        option.yAxis[0].axisLabel.color = color;
        chart.setOption({
            legend: {
                textStyle: {
                    color: color
                }
            },
            tooltip: {
                textStyle: {
                    color: color
                }
            },
            xAxis: [
                {
                    axisLabel: {
                        color: color
                    }
                }
            ],
            yAxis: [
                {
                    axisLabel: {
                        color: color
                    }
                }
            ]
        });
    }

    if (xFormatter)
        option.xAxis[0].axisLabel.formatter = xFormatter;
    chart.setOption(option);
    initData();
    renderData();

    return {
        rebind: function(id) {
            chart.dispose();
            chart = echarts.init(document.getElementById(id));
            chart.setOption(option);
        },
        render: renderData,
        clear: function() {
            for (var i = 0; i < data.length; i++)
                data[i].data = [];
            chart.setOption({
                series: data
            });
        },
        updateFontSize: updateFontSize,
        updateFontColor: updateFontColor,
        update: update,
        load: function(values) {
            initData();
            for (var i = 0; i < values.length; i++) {
                for (var key in values[i].data) {
                    var legend = key in translatedLabels ? translatedLabels[key] : key,
                        p = legends.indexOf(legend);
                    if (p == -1) {
                        legends.push(legend);
                        var s = createSeries(legend);
                        s.data.push([
                            new Date(values[i].time),
                            values[i].data[key]
                        ]);
                        data.push(s);
                    } else {
                        if (data[p].data.length >= max_len)
                            data[p].data.shift();
                        data[p].data.push([
                            new Date(values[i].time),
                            values[i].data[key]
                        ]);
                    }
                }
            }
            renderData();
        }
    };
};

var SingleBarChart = function(id, name, vertical, yAxisLabelFormatter) {
    var chart = echarts.init(document.getElementById(id)),
        bars = [],
        labels = [],
        data = [],
        option = {
            color: COLOR_ORDER,
            animation: false,
            legend: {
                textStyle: {
                    color: COLOR_TEXT
                },
                data: [name]
            },
            tooltip: {
                show: true,
                formatter: '{b}: {c}'
            },
            grid: {
                containLabel: true,
                bottom: 10
            },
            series: [
                {
                    name: name,
                    type: 'bar',
                    barMaxWidth: 30,
                    itemStyle: {
                        opacity: 0.9
                    },
                    data: data
                }
            ]
        },
        valueAxis = {
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
                formatter: yAxisLabelFormatter
            }
        },
        categoryAxis = {
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
            },
            data: labels
        };
    if (vertical) {
        option.yAxis = categoryAxis;
        option.xAxis = valueAxis;
    } else {
        option.xAxis = categoryAxis;
        option.yAxis = valueAxis;
    }

    chart.setOption(option);

    function updateFontSize(size) {
        option.legend.textStyle.fontSize = size;
        if (option.tooltip.textStyle)
            option.tooltip.textStyle.fontSize = size;
        else
            option.tooltip.textStyle = {fontSize: size};
        option.yAxis.axisLabel.fontSize = size;
        option.xAxis.axisLabel.fontSize = size;
        chart.setOption({
            legend: {
                textStyle: {
                    fontSize: size
                }
            },
            tooltip: {
                textStyle: {
                    fontSize: size
                }
            },
            yAxis: {
                axisLabel: {
                    fontSize: size
                }
            },
            xAxis: {
                axisLabel: {
                    fontSize: size
                }
            }
        });
    }

    function updateFontColor(color) {
        option.legend.textStyle.color = color;
        if (option.tooltip.textStyle)
            option.tooltip.textStyle.color = color;
        else
            option.tooltip.textStyle = {color: color};
        option.yAxis.axisLabel.color = color;
        option.xAxis.axisLabel.color = color;
        chart.setOption({
            legend: {
                textStyle: {
                    color: color
                }
            },
            tooltip: {
                textStyle: {
                    color: color
                }
            },
            yAxis: {
                axisLabel: {
                    color: color
                }
            },
            xAxis: {
                axisLabel: {
                    color: color
                }
            }
        });
    }

    function renderData() {
        if (vertical)
            chart.setOption({
                yAxis: {data: labels},
                series: [{data: data}]
            });
        else
            chart.setOption({
                xAxis: {data: labels},
                series: [{data: data}]
            });
    }

    return {
        rebind: function(id) {
            chart.dispose();
            chart = echarts.init(document.getElementById(id));
            chart.setOption(option);
        },
        render: renderData,
        clear: function() {
            bars = [];
            labels = [];
            data = [];
            renderData();
        },
        updateFontSize: updateFontSize,
        updateFontColor: updateFontColor,
        load: function(values) {
            bars = [];
            labels = [];
            data = [];
            if (values) {
                for (var i = 0; i < values.length; i++) {
                    var item = values[i];
                    bars.push(item.key);
                    labels.push(item.label || item.key);
                    data.push(item.value);
                }
            }
            renderData();
        },
        update: function(key, label, value) {
            var p = bars.indexOf(key);
            if (p == -1) {
                bars.push(key);
                labels.push(label || key);
                data.push(value);
            } else {
                if (label) labels[p] = label;
                data[p] = value;
            }
            renderData();
        },
        remove: function(key) {
            var p = bars.indexOf(key);
            if (p > -1) {
                bars.splice(p, 1);
                labels.splice(p, 1);
                data.splice(p, 1);
                renderData();
            }
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
        while (count < 4) {
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
            data = values;
        }
    };
};
