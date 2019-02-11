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
