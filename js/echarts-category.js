
/**
 * 
 * @param {string|Element} container id or element of the container for eCharts canvas
 * @param {object} config
 * ```
 * {
 *      type: 'line'|'bar',
 *      vertical: true|false, // how bars are aligned
 *      valueLabelFormatter: function(v) {},
 *      fontSize: [number],
 *      theme: {
 *          colors: [],
 *          textColor: '',
 *          lineColor: ''
 *      }
 * }
 * ```
 */
function CategoryChart(container, config) {
    var chart,
        seriesLabels = [],
        seriesKeys = [],
        categoryLabels = [],
        categoryKeys = [], // e.g. bars, ponits on line
        series = [],
        options = {
            animation: false,
            legend: { data: seriesLabels },
            tooltip: {
                show: true,
                formatter: function(params) {
                    // '{b}: {c}'
                    if (config.valueLabelFormatter && typeof(config.valueLabelFormatter) == 'function') {
                        return params.name + ': ' + config.valueLabelFormatter(params.value);
                    }
                    return params.name + ': ' + params.value;
                }
            },
            grid: {
                containLabel: true,
                bottom: 10
            },
            series: series
        },
        valueAxis = {
            axisLabel: {
                formatter: config.valueLabelFormatter
            }
        },
        categoryAxis = {
            data: categoryLabels
        };

    function createEcharts(c) {
        if (typeof c == 'string')
            return echarts.init(document.getElementById(c));
        else
            return echarts.init(c);
    }

    if (config.type == 'bar' && config.vertical) {
        options.yAxis = categoryAxis;
        options.xAxis = valueAxis;
    } else {
        options.xAxis = categoryAxis;
        options.yAxis = valueAxis;
    }

    chart = createEcharts(container);
    chart.setOption(options);
    updateFontSize(config.fontSize);
    updateTheme(config.theme);

    function renderData() {
        if (config.type == 'bar' && config.vertical) {
            chart.setOption({
                legend: { data: seriesLabels },
                yAxis: { data: categoryLabels },
                series: series
            });
        } else {
            chart.setOption({
                legend: { data: seriesLabels },
                xAxis: { data: categoryLabels },
                series: series
            });
        }
    }

    function createSeries(skey, name) {
        var s = {
            name: name,
            type: config.type || 'line',
            barMaxWidth: 30,
            itemStyle: { opacity: 0.9 },
            data: []
        };
        seriesKeys.push(skey);
        seriesLabels.push(name);
        series.push(s);
        return s;
    }

    function removeSeries(skey) {
        var i = seriesKeys.indexOf(skey);
        if (i > -1) {
            seriesKeys.splice(i, 1);
            seriesLabels.splice(i, 1);
            series.splice(i, 1);
        }
    }

    function removeCategory(key) {
        var i = categoryKeys.indexOf(key);
        if (i > -1) {
            for (var s = 0; s < series.length; s++)
                series[s].data.splice(i, 1);
            categoryKeys.splice(i, 1);
            categoryLabels.splice(i, 1);
        }
    }

    function checkLen() {
        if (config.maxLen && config.maxLen != Infinity && categoryKeys.length >= config.maxLen) {
            categoryKeys.shift();
            categoryLabels.shift();
            for (var s = 0; s < series.length; s++)
                series[s].data.shift();
        }
    }

    function update(category, data) {
        var key = typeof category == 'string' ? category : category.key,
            c = categoryKeys.indexOf(key),
            skey, s;
        if (c == -1) {
            checkLen();
            categoryKeys.push(key);
            categoryLabels.push(category.label || key);
            if (typeof data == 'object') {
                for (skey in data) {
                    s = seriesKeys.indexOf(skey);
                    if (s > -1)
                        series[s].data.push(data[skey]);
                }
            } else if (series.length == 1) {
                series[0].data.push(data);
            }
        } else {
            if (typeof data == 'object') {
                for (skey in data) {
                    s = seriesKeys.indexOf(skey);
                    if (s > -1)
                        series[s].data[c] = data[skey];
                }
            } else if (series.length == 1) {
                series[0].data[c] = data;
            }
        }
    }

    /**
     * initialize chart data.
     * @param {object} sdata  translations of series labels/legends
     */
    function init(sdata) {
        categoryKeys = [];
        categoryLabels.splice(0, categoryLabels.length);
        seriesKeys = [];
        seriesLabels.splice(0, seriesLabels.length);
        series.splice(0, series.length);
        for (var skey in sdata)
            createSeries(skey, sdata[skey]);
    }

    function clearData() {
        categoryKeys = [];
        categoryLabels = [];
        for (var s = 0; s < series.length; s++)
            series[s].data.splice(0, series[s].data.length);
    }

    function load(values) {
        if (values) {
            if (Array.isArray ? !Array.isArray(values) : !('length' in values))
                values = [values];
            for (var i = 0; i < values.length; i++) {
                checkLen();
                var item = values[i];
                categoryKeys.push(item.key);
                categoryLabels.push(item.label || item.key);
                if (typeof item.data == 'object') {
                    for (var skey in item.data) {
                        var s = seriesKeys.indexOf(skey);
                        if (s > -1)
                            series[s].data.push(item.data[skey]);
                    }
                } else if (series.length == 1) {
                    series[0].data.push(item.data);
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
        update: function(category, data) {
            update(category, data);
            renderData();
        },
        /**
         * load an array of data to the chart.
         *
         * @param {Array|object} values
         * for example, for data series with internal names: a and b
         * ```
         * [
         *      {key: 'abc', data: {a: 1, b: 2}},
         *      {key: 'bcd', data: {a: 3, b: 4}}
         * ]
         * ```
         * or, if there's only one series of data:
         * ```
         * [
         *      {key: 'abc', data: 1},
         *      {key: 'bcd', data: 3}
         * ]
         * ```
         * or, if a single data item is supplied:
         * ```
         * {key: 'abc', data: 1}
         * ```
         * it's automatically converted to a single-element array.
         */
        load: function(values) {
            clearData();
            load(values);
            renderData();
        },
        remove: function(category) {
            removeCategory(category);
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
