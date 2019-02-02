
/**
 * 
 * @param {string|Element} container id or element of the container for eCharts canvas
 * @param {object} config 
 * ```
 * {
 *      maxLen: [number],
 *      xLabelFormatter: '',
 *      yLabelFormatter: '',
 *      showPoints: true|false,
 *      fontSize: [number],
 *      theme: {
 *          colors: [],
 *          textColor: '',
 *          lineColor: ''
 *      }
 * }
 * ```
 */
function TimeChart(container, config) {
    var chart,
        seriesKeys = [],
        seriesLabels = [],
        series = [],
        prevTs = null,
        options = {
            animation: false,
            legend: { data: seriesLabels },
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
            xAxis: {
                type: 'time',
                showMinLabel: false,
                showMaxLabel: false
            },
            yAxis: {
                type: 'value',
                minInterval: 1
            },
            series: series
        };

    function createEcharts(c) {
        if (typeof c == 'string')
            return echarts.init(document.getElementById(c));
        else
            return echarts.init(c);
    }

    if (config.xLabelFormatter) {
        if (!options.xAxis.axisLabel)
            options.xAxis.axisLabel = {};
        options.xAxis.axisLabel.formatter = config.xLabelFormatter;
    }
    if (config.yLabelFormatter) {
        if (!options.yAxis.axisLabel)
            options.yAxis.axisLabel = {};
        options.yAxis.axisLabel.formatter = config.yLabelFormatter;
    }
    chart = createEcharts(container);
    chart.setOption(options);
    updateFontSize(config.fontSize);
    updateTheme(config.theme);

    function renderData() {
        chart.setOption({
            legend: { data: seriesLabels },
            series: series
        });
    }

    function createSeries(key, name) {
        var s = {
            name: name,
            type: 'line',
            itemStyle: { normal: { areaStyle: { type: 'default' } } },
            data: []
        };
        if (config.showPoints) {
            s.symbol = 'circle';
            s.symbolSize = 8;
        } else {
            s.symbol = 'none';
        }
        seriesKeys.push(key);
        seriesLabels.push(name);
        series.push(s);
        return s;
    }

    function update(t, data) {
        var tdate = new Date(t);
        if (typeof data == 'object') {
            for (var skey in data) {
                var s = seriesKeys.indexOf(skey);
                if (s > -1) {
                    var arr = series[s].data;
                    if (prevTs != null && prevTs >= t) {
                        for (var p = arr.length - 1; p >= 0; p--) {
                            if (arr[p][0].getTime() == t) {
                                arr[p][1] = data[skey];
                                break;
                            }
                        }
                    } else {
                        if (arr.length >= config.maxLen)
                            arr.shift();
                        arr.push([tdate, data[skey]]);
                    }
                }
            }
        } else if (series.length == 1) {
            var arr = series[0].data;
            if (prevTs != null && prevTs >= t) {
                for (var p = arr.length - 1; p >= 0; p--) {
                    if (arr[p][0].getTime() == t) {
                        arr[p][1] = data;
                        break;
                    }
                }
            } else {
                if (arr.length >= config.maxLen)
                    arr.shift();
                arr.push([tdate, data]);
            }
        }
        prevTs = t;
    }

    function init(sdata) {
        prevTs = null;
        seriesKeys = [];
        seriesLabels = [];
        series = [];
        for (var skey in sdata)
            createSeries(skey, sdata[skey]);
    }

    function clearData() {
        prevTs = null;
        for (var s = 0; s < series.length; s++)
            series[s].data = [];
    }

    function load(values) {
        if (values) {
            for (var i = 0; i < values.length; i++) {
                var item = values[i],
                    tdate = new Date(item.key);
                if (typeof item.data == 'object') {
                    for (var skey in item.data) {
                        var s = seriesKeys.indexOf(skey);
                        if (s > -1) {
                            var arr = series[s].data;
                            if (arr.length >= config.maxLen)
                                arr.shift();
                            arr.push([tdate, item.data[skey]]);
                        }
                    }
                } else if (series.length == 1) {
                    var arr = series[0].data;
                    if (arr.length >= config.maxLen)
                        arr.shift();
                    arr.push([tdate, item.data]);
                }
            }
        }
    }

    function updateFontSize(size) {
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
            xAxis: {
                axisLabel: {
                    fontSize: size
                }
            },
            yAxis: {
                axisLabel: {
                    fontSize: size
                }
            }
        });
        config.fontSize = size;
    }

    function updateTheme(theme) {
        chart.setOption({
            color: theme.colors,
            legend: {
                textStyle: {
                    color: theme.textColor
                }
            },
            tooltip: {
                textStyle: {
                    color: theme.textColor
                }
            },
            xAxis: {
                splitLine: {
                    lineStyle: {
                        color: [theme.lineColor]
                    }
                },
                axisLine: {
                    lineStyle: {
                        color: theme.lineColor
                    }
                },
                axisLabel: {
                    color: theme.textColor
                }
            },
            yAxis: {
                splitLine: {
                    lineStyle: {
                        color: [theme.lineColor]
                    }
                },
                axisLine: {
                    lineStyle: {
                        color: theme.lineColor
                    }
                },
                axisLabel: {
                    color: theme.textColor
                }
            }
        });
        config.theme = theme;
    }

    return {
        init: init,
        rebind: function() {
            chart.dispose();
            chart = createEcharts(container);
            chart.setOption(options);
            updateFontSize(config.fontSize);
            updateTheme(config.theme);
            renderData();
        },
        update: function(t, data) {
            update(t, data);
            renderData();
        },
        load: function(values) {
            clearData();
            load(values);
            renderData();
        },
        clear: function() {
            clearData();
            renderData();
        },
        updateFontSize: updateFontSize,
        updateTheme: updateTheme
    };
}
