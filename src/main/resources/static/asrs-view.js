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
            fill: config.emptyLocColor,
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
        this.move = function(pos) {
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
                };
            }
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
            fill: config.emptyLocColor,
            stroke: config.locBorderColor
        });
        this.value = 0;
        this.piler = piler;
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
        locate: function() {},
        retrieve: function(x, y) {
            var loc = asrsArray[y][x];
            loc.piler.move(x);
        },
        store: function() {}
    };

}
