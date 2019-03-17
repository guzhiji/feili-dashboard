
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
        update: function(key, value) {
            update(key, value);
            renderData();
        },
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
