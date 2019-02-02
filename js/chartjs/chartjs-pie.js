
function PieChart(container, config) {

    function createChart(c, o) {
        var container = typeof(c) == 'string' ?
            document.getElementById(c) : c,
            canvas = container.firstElementChild;
        if (!canvas || canvas.tagName == 'canvas') {
            canvas = document.createElement('canvas');
            container.appendChild(canvas);
        }
        return new Chart(canvas.getContext('2d'), o);
    }

    var options = {
            type: 'pie',
            data: {
                datasets: [
                    {
                        data: [],
                        backgroundColor: [],
                        label: 'Dataset 1'
                    }
                ],
                labels: []
            },
            options: {
                responsive: true
            }
        },
        chart = createChart(container, options),
        pieKeys = [];

    function init(trans) {
        var ds = options.data.datasets[0];
        pieKeys = [];
        options.data.labels = [];
        ds.data = [];
        ds.backgroundColor = [];
        for (var key in trans) {
            var c = pieKeys.length % config.colors.length;
            pieKeys.push(key);
            options.data.labels.push(trans[key]);
            ds.data.push(0);
            ds.backgroundColor.push(config.colors[c]);
        }
    }

    function update(key, value) {
        var i = pieKeys.indexOf(key);
        if (i > -1)
            options.data.datasets[0].data[i] = value;
    }

    function load(values) {
        if (values) {
            for (var key in values)
                update(key, values[key]);
        }
    }

    function clearData() {
        var ds = options.data.datasets[0];
        ds.data = ds.data.map(function() { return 0; });
    }

    function remove(key) {
        var ds = options.data.datasets[0],
            i = pieKeys.indexOf(key);
        if (i > -1) {
            pieKeys.splice(i, 1);
            options.data.labels.splice(i, 1);
            ds.data.splice(i, 1);
            ds.backgroundColor.pop();
        }
    }

    function add(key, label, value) {
        if (pieKeys.indexOf(key) == -1) {
            var ds = options.data.datasets[0],
                c = pieKeys.length % config.colors.length;
            pieKeys.push(key);
            options.data.labels.push(label);
            ds.data.push(value);
            ds.backgroundColor.push(config.colors[c]);
        }
    }

    return {
        init: init,
        update: function(key, value) {
            update(key, value);
            chart.update();
        },
        load: function(values) {
            clearData();
            load(values);
            chart.update();
        },
        clear: function() {
            clearData();
            chart.update();
        },
        add: function(key, label, value) {
            add(key, label, value);
            chart.update();
        },
        remove: function(key) {
            remove(key);
            chart.update();
        }
    };

}
