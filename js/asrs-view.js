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

    var svgns = 'http://www.w3.org/2000/svg',
        xlinkns = 'http://www.w3.org/1999/xlink',
        ATTR_MAP = {
            'className': 'class',
            'svgHref': 'href'
        },
        NS_MAP = {
            'svgHref': xlinkns
        };

    function setSVG(elem, attributes) {
        for (var attribute in attributes) {
            var name = (attribute in ATTR_MAP ? ATTR_MAP[attribute] : attribute);
            var value = attributes[attribute];
            if (attribute in NS_MAP)
                elem.setAttributeNS(NS_MAP[attribute], name, value);
            else
                elem.setAttribute(name, value);
        }
    }

    function makeSVG(tag, attributes) {
        var elem = document.createElementNS(svgns, tag);
        setSVG(elem, attributes);
        return elem;
    }

    var padding = 10,
        rowMargin = 2,
        locWidth,
        locCornerRadius,
        viewWidth,
        asrsView = document.getElementById(config.viewId),
        asrsArray = [],
        asrsPilers = [],
        asrsRowGroups = [];

    function calcViewHeight() {
        // 3 = 2 rows + 1 piler
        return (locWidth + rowMargin) * 3
            * config.rowPairsPerGroup * config.rowGroups
            + config.rowGroupMargin * (config.rowGroups - 1);
    }

    function calcViewWidth() {
        return locWidth * (config.cols + 1) + padding * 2;
    }

    function calcLocWidthByViewHeight(h) {
        var noGroupMargin = h - config.rowGroupMargin * (config.rowGroups - 1);
        var rowHeight = noGroupMargin / config.rowGroups / config.rowPairsPerGroup / 3;
        return rowHeight - rowMargin;
    }

    function calcLocWidthByViewWidth(w) {
        return (w - padding * 2) / (config.cols + 1);
    }

    function calcLocWidth() {
        var w, h, lw1, lw2;
        if (asrsView.hasAttribute('width')) {
            w = asrsView.getAttribute('width');
            lw1 = calcLocWidthByViewWidth(w);
            if (asrsView.hasAttribute('height')) {
                // both
                h = asrsView.getAttribute('height');
                lw2 = calcLocWidthByViewHeight(h);
                return lw1 < lw2 ? lw1 : lw2;
            } else {
                // width only
                return lw1;
            }
        } else if (asrsView.hasAttribute('height')) {
            // height only
            h = asrsView.getAttribute('height');
            return calcLocWidthByViewHeight(h);
        } else {
            // no sizing properties => use default
            return 10;
        }
    }

    function setViewSize() {
        locWidth = calcLocWidth();
        locCornerRadius = locWidth / 3 > 5 ? 5: (locWidth / 3);
        viewWidth = calcViewWidth();
        asrsView.setAttribute('width', viewWidth);
        asrsView.setAttribute('height', calcViewHeight());
    }
    setViewSize();

    function AsrsPiler(pos) {

        this.loaded = false;
        function _resize() {
            var x = padding / 2 + locWidth * pos;
            var y = locWidth + rowMargin;
            var d = 2 * 4 > locWidth / 2 ? 1 : 2;
            this.elPilerAttrs = {transform: 'translate(' +
                    x + ', ' + y + ')'};
            this.elPilerInnerAttrs = {
                x: 0,
                y: 2 * d,
                rx: 2,
                ry: 2,
                width: padding + locWidth * 2,
                height: locWidth - 4 * d,
                fill: config.pilerColor,
                'stroke-width': 0
            };
            this.elPilerMainAttrs = {
                x: padding / 2,
                y: d,
                rx: 2,
                ry: 2,
                width: locWidth * 2,
                height: locWidth - 2 * d,
                fill: config.pilerColor,
                'stroke-width': 0
            };
            this.elPilerLocAttrs = {
                x: padding / 2,
                y: 0,
                rx: locCornerRadius,
                ry: locCornerRadius,
                width: locWidth,
                height: locWidth,
                fill: this.loaded ?
                    config.locUtilizationColors[0] :
                    config.locEmptyColor,
                stroke: config.locBorderColor,
                'stroke-width': 1
            };
        }
        _resize.call(this);

        var el = makeSVG('g', this.elPilerAttrs);
        this.elInner = makeSVG('rect', this.elPilerInnerAttrs);
        this.elMain = makeSVG('rect', this.elPilerMainAttrs);
        this.elLoc = makeSVG('rect', this.elPilerLocAttrs);
        el.appendChild(this.elInner);
        el.appendChild(this.elMain);
        el.appendChild(this.elLoc);
        this.el = el;
        this.pos = pos;

        this.resize = function() {
            _resize.call(this);
            setSVG(this.el, this.elPilerAttrs);
            setSVG(this.elInner, this.elPilerInnerAttrs);
            setSVG(this.elMain, this.elPilerMainAttrs);
            setSVG(this.elLoc, this.elPilerLocAttrs);
        };

        this.load = function() {
            this.loaded = true;
            this.elLoc.setAttribute('fill', config.locUtilizationColors[0]);
        };

        this.unload = function() {
            this.loaded = false;
            this.elLoc.setAttribute('fill', config.locEmptyColor);
        };

        var self = this;
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

    }

    function AsrsLoc(i, whichRow, piler) {
        this.piler = piler;
        this.value = 0;

        function getLocColor(value) {
            if (value == 0)
                return config.locEmptyColor;
            return config.locUtilizationColors[value - 1];
        }

        function _resize() {
            this.elAttrs = {
                x: padding + i * locWidth,
                y: whichRow == 0 ? 0 : ((locWidth + rowMargin) * 2),
                rx: locCornerRadius,
                ry: locCornerRadius,
                width: locWidth,
                height: locWidth,
                fill: getLocColor(this.value),
                stroke: config.locBorderColor
            };
        }
        _resize.call(this);

        this.el = makeSVG('rect', this.elAttrs);

        this.resize = function() {
            _resize.call(this);
            setSVG(this.el, this.elAttrs);
        };

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
                this.el.setAttribute('fill', getLocColor(this.value));
                return true;
            }
            return false;
        };

    }

    function AsrsRowPair(n) {

        function _resize() {
            this.elAttrs = {transform: 'translate(0, ' +
                ((locWidth + rowMargin) * n * 3) + ')'};
            this.elRowTrackAttrs = {
                x1: 0,
                y1: 1.5 * locWidth + rowMargin,
                x2: viewWidth,
                y2: 1.5 * locWidth + rowMargin,
                stroke: config.trackColor,
                'stroke-width': 1
            };
        }
        _resize.call(this);

        (function() {
            var row1 = [], row2 = [];
            var piler = new AsrsPiler(0);
            var el = makeSVG('g', this.elAttrs);
            this.elRowTrack = makeSVG('line', this.elRowTrackAttrs);
            el.appendChild(this.elRowTrack);
            el.appendChild(piler.el);
            for (var i = 0; i < config.cols; i++) {
                var loc1 = new AsrsLoc(i, 0, piler),
                    loc2 = new AsrsLoc(i, 1, piler);
                el.appendChild(loc1.el);
                el.appendChild(loc2.el);
                row1.push(loc1);
                row2.push(loc2);
            }
            this.el = el;
            this.piler = piler;
            this.row1 = row1;
            this.row2 = row2;
            asrsPilers.push(piler);
            asrsArray.push(row1);
            asrsArray.push(row2);
        }.bind(this))();

        this.resize = function() {
            var i;
            _resize.call(this);
            setSVG(this.el, this.elAttrs);
            setSVG(this.elRowTrack, this.elRowTrackAttrs);
            this.piler.resize();
            for (i = 0; i < this.row1.length; i++) this.row1[i].resize();
            for (i = 0; i < this.row2.length; i++) this.row2[i].resize();
        };

    }

    function AsrsRowGroup(n) {
        function _resize() {
            this.elAttrs = {
                transform: 'translate(0, ' + (((locWidth + rowMargin) * 3
                * config.rowPairsPerGroup
                + config.rowGroupMargin) * n) + ')'};
        }
        _resize.call(this);

        this.el = makeSVG('g', this.elAttrs);

        (function() {
            var pairs = [];
            for (var i = 0; i < config.rowPairsPerGroup; i++) {
                var p = new AsrsRowPair(i);
                this.el.appendChild(p.el);
                pairs.push(p);
            }
            this.pairs = pairs;
        }.bind(this))();

        this.resize = function() {
            _resize.call(this);
            setSVG(this.el, this.elAttrs);
            for (var i = 0; i < this.pairs.length; i++) this.pairs[i].resize();
        };

    }

    function locate(x, y) {
        if (y in asrsArray && x in asrsArray[y])
            return asrsArray[y][x];
        return null;
    }

    function resize() {
        setViewSize();
        for (var i = 0; i < asrsRowGroups.length; i++)
            asrsRowGroups[i].resize();
    }

    (function() {
        for (var i = 0; i < config.rowGroups; i++) {
            var g = new AsrsRowGroup(i);
            asrsView.appendChild(g.el);
            asrsRowGroups.push(g);
        }
    })();

    return {
        el: asrsView,
        init: function() {}, // init with storage data
        getArray: function() { return asrsArray; },
        getPilers: function() { return asrsPilers; },
        locate: locate,
        resize: resize,
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
