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

var dashboard = (function($) {
    // states
    var stomp = null,
        stompConnected = false,
        dataRenderers = {},
        resultSources = {},
        messageSources = {};
    // extensions
    var dataRendererInitializers = {},
        dataHandlers = {},
        valueFormatters = {
            'int': parseInt,
            'float': parseFloat,
            '1-minus-x': function(value) {
                var f = parseFloat(value);
                if (f < 0)
                    f = 0.0;
                else if (f > 1)
                    f = 1.0;
                return 1.0 - f;
            },
            '100-minus-x': function(value) {
                var f = parseFloat(value);
                if (f < 0)
                    f = 0.0;
                else if (f > 100)
                    f = 100.0;
                return 100.0 - f;
            }
        };

    function createFieldTransMap(fields) {
        var out = {};
        for (var f = 0; f < fields.length; f++)
            out[fields[f].internalName] = fields[f].name;
        return out;
    }

    function formatAllFields(fields, data) {
        var out = {}, field, key, f;
        for (f = 0; f < fields.length; f++) {
            field = fields[f];
            key = field.internalName;
            if (key in data) {
                if (field.formatter && field.formatter in valueFormatters)
                    out[key] = valueFormatters[field.formatter](data[key]);
                else
                    out[key] = data[key];
            }
        }
        return out;
    }

    function ResultSource(path, handler, rate) {
        var blks = [], timer = null, started = false;

        /**
         * check whether all blocks have message sources
         */
        function checkMessageSource() {
            for (var b = 0; b < blks.length; b++) {
                if (!blks[b].messageSource)
                    return false;
            }
            return true;
        }

        function loadData() {
            if (handler in dataHandlers) {
                $.get(path, function(data) {
                    for (var b = 0; b < blks.length; b++) {
                        if (blks[b].id in dataRenderers) {
                            dataHandlers[handler](dataRenderers[blks[b].id], blks[b], {
                                cmd: 'load',
                                data: data
                            });
                        }
                    }
                });
            }
        }

        function stop() {
            if (timer) {
                clearInterval(timer);
                timer = null;
            }
            if (started) {
                for (var b = 0; b < blks.length; b++) {
                    if (blks[b].id in dataRenderers)
                        dataRenderers[blks[b].id].clear();
                }
                started = false;
            }
        }

        function start() {
            if (started) stop();
            loadData();
            if (rate > 0 && !checkMessageSource()) {
                // start periodical loading if:
                // - rate is set
                // - there's at least one block doesn't have message source
                timer = setInterval(loadData, rate);
            }
            started = true;
        }

        return {
            addBlock: function(b) {
                blks.push(b);
            },
            isStarted: function() {
                return started;
            },
            start: start,
            stop: stop
        };
    }

    function MessageSource(handler) {
        var blks = [];

        function handle(msg) {
            var data = JSON.parse(msg.body);
            if (handler in dataHandlers) {
                for (var b = 0; b < blks.length; b++) {
                    if (blks[b].id in dataRenderers) {
                        dataHandlers[handler](dataRenderers[blks[b].id],
                            blks[b], data);
                    }
                }
            }
        }

        return {
            addBlock: function(b) {
                blks.push(b);
            },
            handle: handle
        };
    }

    function startLoadingResultSources() {
        for (var path in resultSources)
            resultSources[path].start();
    }

    function stopLoadingResultSources() {
        for (var path in resultSources)
            resultSources[path].stop();
    }

    function subscribeMessageSources() {
        if (stomp) {
            for (var dest in messageSources)
                stomp.subscribe(dest, messageSources[dest].handle);
        }
    }

    function connectStomp() {
        var ws = new SockJS('/sockjs');
        stomp = Stomp.over(ws);
        stomp.debug = null;
        stomp.connect({}, function() {
            // on success
            stompConnected = true;
            startLoadingResultSources();
            subscribeMessageSources();
        }, function() {
            // on error
            stompConnected = false;
            stopLoadingResultSources();
            // TODO raise event
            setTimeout(connectStomp, 1000);
        });
    }

    function init(data) {
        var b, blk, path, mons = {};
        for (b = 0; b < data.monitors.length; b++)
            mons[data.monitors[b].id] = data.monitors[b];
        for (b = 0; b < data.blocks.length; b++) {
            blk = data.blocks[b];
            // init data renderer components
            if (blk.dataRenderer in dataRendererInitializers) {
                dataRenderers[blk.id] = dataRendererInitializers[blk.dataRenderer](blk);
            }
            if (blk.monitorId) {
                // register result sources
                if (blk.resultSource) {
                    path = '/dashboard/monitor/' + blk.monitorId +
                        '/result/' + blk.resultSource + '.json';
                    if (!(path in resultSources))
                        resultSources[path] = ResultSource(
                            path,
                            mons[blk.monitorId].resultSources[blk.resultSource],
                            mons[blk.monitorId].execRate);
                    resultSources[path].addBlock(blk);
                }
                // register message sources
                if (blk.messageSource) {
                    path = '/dashboard/monitor/' + blk.monitorId +
                        '/' + blk.messageSource;
                    if (!(path in messageSources))
                        messageSources[path] = MessageSource(
                            mons[blk.monitorId].messageSources[blk.messageSource]);
                    messageSources[path].addBlock(blk);
                }
            }
        }
        // connect stomp -> load result sources, subscribe message sources
        connectStomp();
        $(window).on('resize', function() {

            for (var b in dataRenderers) {
                if ('rebind' in dataRenderers[b])
                    dataRenderers[b].rebind();
            }

        });
    }

    return {
        utils: {
            createFieldTransMap: createFieldTransMap,
            formatAllFields: formatAllFields,
            formatValue: function(formatter, value) {
                if (formatter && formatter in valueFormatters)
                    return valueFormatters[formatter](value);
                return value;
            }
        },
        registerDataRenderer: function(name, initializer) {
            dataRendererInitializers[name] = initializer;
        },
        registerDataHandler: function(name, handler) {
            dataHandlers[name] = handler;
        },
        registerValueFormatter: function(name, formatter) {
            valueFormatters[name] = formatter;
        },
        isConnected: function() {
            return stompConnected;
        },
        load: function(url) {
            $.get(url, init);
        }
    };
})(jQuery);