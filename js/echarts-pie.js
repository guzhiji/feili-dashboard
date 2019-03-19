
/**
 * 
 * @param {string|Element} container id or element of the container for eCharts canvas
 * @param {object} config
 * ```
 * {
 *      fontSize: [number],
 *      theme: {
 *          colors: [],
 *          textColor: ''
 *      }
 * }
 * ```
 */
function PieChart(container, config) {
    var chart,
        pieKeys = [],
        pieLabels = [],
        data = [],
        options = {
            legend: { data: pieLabels },
            series: [
                {
                    type: 'pie',
                    radius: '40%',
                    label: {
                        show: true,
                        formatter: '{b}: {c} \n {d}%'
                    },
                    itemStyle: {
                        opacity: 0.9
                    },
                    data: data
                }
            ]
        };

    function createEcharts(c) {
        if (typeof c == 'string')
            return echarts.init(document.getElementById(c));
        else
            return echarts.init(c);
    }

    chart = createEcharts(container);
    chart.setOption(options);
    updateFontSize(config.fontSize);
    updateTheme(config.theme);

    function renderData() {
        chart.setOption({
            legend: { data: pieLabels },
            series: [ { data: data } ]
        });
    }

    function init(labelTrans) {
        pieKeys = [];
        pieLabels = [];
        data = [];
        for (var key in labelTrans) {
            pieKeys.push(key);
            pieLabels.push(labelTrans[key]);
            data.push({
                name: labelTrans[key],
                value: 0
            });
        }
    }

    function clearData() {
        for (var i = 0; i < data.length; i++)
            data[i].value = 0;
    }

    function update(key, value) {
        if (value && typeof(value) == 'object') {
            clearData();
            load(value);
        } else {
            var i = pieKeys.indexOf(key);
            if (i > -1) data[i].value = value;
        }
    }

    function remove(key) {
        var i = pieKeys.indexOf(key);
        if (i > -1) {
            data.splice(i, 1);
            pieKeys.splice(i, 1);
            pieLabels.splice(i, 1);
        }
    }

    function load(values) {
        if (values) {
            if (Array.isArray ? Array.isArray(values) : 'length' in values)
                values = values[values.length - 1];
            if ('key' in values && 'data' in values && typeof(values.data) == 'object')
                values = values.data;
            for (var key in values) {
                var i = pieKeys.indexOf(key);
                if (i > -1)
                    data[i].value = values[key];
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
            series: [
                {
                    label: {
                        fontSize: size
                    }
                }
            ]
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
            series: [
                {
                    label: {
                        color: theme.textColor
                    }
                }
            ]
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
        /**
         * update a part of the pie or load all values if object is supplied.
         *
         * @param {number|string} key       key that represents a part of the pie
         * @param {number|object} value     the value to be updated to;
         * or if an object is supplied, it's redirected to load(),
         * in which case, key is discarded.
         */
        update: function(key, value) {
            update(key, value);
            renderData();
        },
        /**
         * load data to the chart.
         *
         * @param {object|Array} values
         * each key is mapped to a part of the pie:
         * ```
         * {a: 1, b: 2, c: 3}
         * ```
         * or if in this format:
         * ```
         * {key: 1552980555912, data: {a: 1, b: 2, c: 3}}
         * ```
         * key is discarded and content of data is used instead;
         * or if an array is passed, the last element is applied.
         */
        load: function(values) {
            clearData();
            load(values);
            renderData();
        },
        remove: function(key) {
            remove(key);
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
