function colorRange(from, to, n) {
    if (from[0] == '#') from = from.substr(1, 6);
    if (to[0] == '#') to = to.substr(1, 6);
    var intFrom = [
        parseInt(from.substr(0, 2), 16),
        parseInt(from.substr(2, 2), 16),
        parseInt(from.substr(4, 2), 16)];
    var intTo = [
        parseInt(to.substr(0, 2), 16),
        parseInt(to.substr(2, 2), 16),
        parseInt(to.substr(4, 2), 16)];
    var intDiff = [
        (intTo[0] - intFrom[0] + 1) / n,
        (intTo[1] - intFrom[1] + 1) / n,
        (intTo[2] - intFrom[2] + 1) / n];
    var out = [];
    for (var i = 0; i < n; i++) {
        var r = (intFrom[0] + Math.round(intDiff[0] * i)).toString(16);
        var g = (intFrom[1] + Math.round(intDiff[1] * i)).toString(16);
        var b = (intFrom[2] + Math.round(intDiff[2] * i)).toString(16);
        if (r.length == 1) r = '0' + r;
        if (g.length == 1) g = '0' + g;
        if (b.length == 1) b = '0' + b;
        out.push('#' + r + g + b);
    }
    return out;
}
function asrsView(config) {

    var svgns = "http://www.w3.org/2000/svg",
        xlinkns = "http://www.w3.org/1999/xlink",
        ATTR_MAP = {
            "className": "class",
            "svgHref": "href"
        },
        NS_MAP = {
            "svgHref": xlinkns
        };

    function makeSVG(tag, attributes) {
        var elem = document.createElementNS(svgns, tag);
        for (var attribute in attributes) {
            var name = (attribute in ATTR_MAP ? ATTR_MAP[attribute] : attribute);
            var value = attributes[attribute];
            if (attribute in NS_MAP)
                elem.setAttributeNS(NS_MAP[attribute], name, value);
            else
                elem.setAttribute(name, value);
        }
        return elem;
    }

    var padding = 10,
        rowMargin = 2,
        locWidth = (config.viewWidth - padding * 2) / (config.cols + 1),
        asrsArray = [],
        asrsPilers = [];

    function createPiler(attrs) {
        var d = 2 * 4 > locWidth / 2 ? 1 : 2;
        var elPiler = makeSVG('g', attrs || {});
        var elPilerInner = makeSVG('rect', {
            x: 0,
            y: 2 * d,
            rx: 2,
            ry: 2,
            width: padding + locWidth * 2,
            height: locWidth - 4 * d,
            fill: config.pilerColor,
            'stroke-width': 0
        });
        var elPilerMain = makeSVG('rect', {
            x: padding / 2,
            y: d,
            rx: 2,
            ry: 2,
            width: locWidth * 2,
            height: locWidth - 2 * d,
            fill: config.pilerColor,
            'stroke-width': 0
        });
        var elPilerLoc = makeSVG('rect', {
            x: padding / 2,
            y: 0,
            rx: 5,
            ry: 5,
            width: locWidth,
            height: locWidth,
            fill: config.locEmptyColor,
            stroke: config.locBorderColor,
            'stroke-width': 1
        });
        elPiler.appendChild(elPilerInner);
        elPiler.appendChild(elPilerMain);
        elPiler.appendChild(elPilerLoc);
        return elPiler;
    }

    function AsrsPiler(pos) {
        this.el = makeSVG('use', {
            svgHref: '#asrs-piler',
            transform: 'translate(' + (padding / 2 + locWidth * pos) +
                ', ' + (locWidth + rowMargin) + ')'
        });
        this.pos = pos;
        var self = this;
        this.queue = [];
        this.currentWork = null;
        this.timer = setInterval(function() {
            if (!self.currentWork) {
                var instruction = self.queue.shift();
                if (instruction) {
                    self.currentWork = instruction;
                    switch (instruction[0]) {
                        case 'r':
                            if (instruction[2].canRemove()) {
                                instruction[2].blink();
                                self.moveTo(instruction[1], function() {
                                    instruction[2].remove();
                                    self.load();
                                    self.moveTo(0, function() {
                                        self.unload();
                                        self.currentWork = null;
                                    });
                                });
                            } else { // discard
                                self.currentWork = null;
                            }
                            break;
                        case 's':
                            if (instruction[2].canAdd()) {
                                self.moveTo(0, function() {
                                    instruction[2].blink();
                                    self.load();
                                    self.moveTo(instruction[1], function() {
                                        self.unload();
                                        instruction[2].add();
                                        self.currentWork = null;
                                    });
                                });
                            } else { // discard
                                self.currentWork = null;
                            }
                            break;
                    }
                }
            }
        }, 100);
        this.moveTo = function(pos, done) {
            var opos = self.pos;
            if (pos != opos) {
                var animation = self.el.animate({transform: [
                    'translate(' + (padding / 2 + locWidth * opos) + 'px,' +
                        (locWidth + rowMargin) + 'px)',
                    'translate(' + (padding / 2 + locWidth * pos) + 'px,' +
                        (locWidth + rowMargin) + 'px)'
                ]}, {
                    duration: 2000,
                    easing: 'ease-in-out'
                });
                animation.onfinish = function() {
                    self.el.setAttribute('transform',
                        'translate(' + (padding / 2 + locWidth * pos) +
                            ', ' + (locWidth + rowMargin) + ')');
                    self.pos = pos;
                    if (done) done();
                };
            } else if (done) {
                done();
            }
        };
        this.load = function() {
        };
        this.unload = function() {
        };
    }

    function AsrsLoc(i, y, piler) {
        this.el = makeSVG('rect', {
            x: padding + i * locWidth,
            y: y,
            rx: 5,
            ry: 5,
            width: locWidth,
            height: locWidth,
            fill: config.locEmptyColor,
            stroke: config.locBorderColor
        });
        this.piler = piler;
        this.value = 0;
        this.blink = function() {
            this.el.animate({stroke: [
                config.locBorderColor,
                config.locBlinkColor
            ]}, {
                duration: 500,
                iterations: 8
            });
        };
        this.canAdd = function() {
            return this.value in config.locUtilizationColors;
        };
        this.add = function() {
            if (this.canAdd()) {
                this.el.setAttribute('fill',
                    config.locUtilizationColors[this.value++]);
                return true;
            }
            return false;
        };
        this.canRemove = function() {
            return this.value > 0;
        };
        this.remove = function() {
            if (this.canRemove()) {
                this.value--;
                if (this.value == 0) {
                    this.el.setAttribute('fill', config.locEmptyColor);
                } else {
                    this.el.setAttribute('fill',
                        config.locUtilizationColors[this.value - 1]);
                }
                return true;
            }
            return false;
        };
    }

    function createRowPair(attrs) {
        var row1 = [], row2 = [];
        var elRowPair = makeSVG('g', attrs || {});
        var piler = new AsrsPiler(0);
        var elRowTrack = makeSVG('line', {
            x1: 0,
            y1: 1.5 * locWidth + rowMargin,
            x2: config.viewWidth,
            y2: 1.5 * locWidth + rowMargin,
            stroke: config.trackColor
        });
        elRowPair.appendChild(elRowTrack);
        elRowPair.appendChild(piler.el);
        for (var i = 0; i < config.cols; i++) {
            var loc1 = new AsrsLoc(i, 0, piler),
                loc2 = new AsrsLoc(i, (locWidth + rowMargin) * 2, piler);
            elRowPair.appendChild(loc1.el);
            elRowPair.appendChild(loc2.el);
            row1.push(loc1);
            row2.push(loc2);
        }
        asrsPilers.push(piler);
        asrsArray.push(row1);
        asrsArray.push(row2);
        return elRowPair;
    }

    function createRowGroup(attrs) {
        var elGroup = makeSVG('g', attrs || {});
        for (var i = 0; i < config.rowPairsPerGroup; i++) {
            elGroup.appendChild(createRowPair({
                transform: 'translate(0, ' + ((locWidth + rowMargin) * i * 3) + ')'
            }));
        }
        return elGroup;
    }

    function locate(x, y) {
        if (y in asrsArray && x in asrsArray[y])
            return asrsArray[y][x];
        return null;
    }

    var asrsView = document.getElementById(config.viewId);
    asrsView.setAttribute('width', config.viewWidth);
    asrsView.setAttribute('height',
        ((locWidth + rowMargin) * 3
        * config.rowPairsPerGroup * config.rowGroups
        + config.rowGroupMargin * (config.rowGroups - 1)));

    (function() {
        var elDefs = makeSVG('defs', {});
        elDefs.appendChild(createPiler({id: 'asrs-piler'}));
        asrsView.appendChild(elDefs);
        for (var i = 0; i < config.rowGroups; i++) {
            asrsView.appendChild(createRowGroup({
                transform: 'translate(0, ' + (((locWidth + rowMargin) * 3
                * config.rowPairsPerGroup
                + config.rowGroupMargin) * i) + ')'
            }));
        }
    })();

    return {
        el: asrsView,
        init: function() {}, // init with storage data
        getArray: function() { return asrsArray; },
        getPilers: function() { return asrsPilers; },
        locate: locate,
        retrieve: function(x, y) {
            var loc = locate(x, y);
            if (loc != null) {
                loc.piler.queue.push(['r', x, loc]);
            }
        },
        store: function(x, y) {
            var loc = locate(x, y);
            if (loc != null) {
                loc.piler.queue.push(['s', x, loc]);
            }
        }
    };

}
