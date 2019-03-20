
/**
 * 
 * @param {string|Element} container    id of the table element.
 * @param {object} $                    jQuery object
 * @param {object} config
 * ```
 * {
 *      pagerId: 'element id where page/page-count is displayed',
 *      fields: [
 *          {
 *              key: 'internal name of the field',
 *              name: 'displayed field name',
 *              formatter: function(value) {}
 *          },
 *          ...
 *      ],
 *      autoGrow: boolean, // rows auto-grow to fit screen size
 *      maxRows: number,   // row limit for one page if autoGrow=false
 *      minRows: number,   // keep blank rows if there're fewer rows of data
 *      bottomSpace: pixels, // bottom space to leave if rows auto-grow
 *      pageSpeed: number    // milliseconds before turning to the next page
 * }
 * ```
 */
function DataTable(container, $, config) {
    var table = getTable(container),
        pager = null,
        tbody = null,
        page = 0,
        data = [],
        dataKeys = null,
        jobQueue = [];

    if (config.pagerId)
        pager = $('#' + config.pagerId);

    // init fields
    (function() {
        var thead = table.find('>thead'),
            hrow = $('<tr></tr>'),
            col, f;
        if (thead.length == 0) {
            thead = $('<thead></thead>');
            thead.append(hrow);
            table.append(thead);
        } else {
            thead.empty();
            thead.append(hrow);
        }
        for (f = 0; f < config.fields.length; f++) {
            col = $('<th></th>');
            col.text(config.fields[f].name);
            hrow.append(col);
        }
    })();

    function getTable(c) {
        if (typeof c == 'string')
            return $('#' + c);
        return $(c);
    }

    function createRow() {
        var r = $('<tr></tr>'),
            i;
        for (i = 0; i < config.fields.length; i++)
            r.append('<td>&nbsp;</td>');
        tbody.append(r);
        return r;
    }

    function initBody() {
        tbody = table.find('>tbody');
        if (tbody.length == 0) {
            tbody = $('<tbody></tbody>');
            table.append(tbody);
        } else {
            tbody.empty();
        }
        if (config.autoGrow) {
            var tOffset = table.offset(),
                wH = $(window).height(),
                count = 0;
            while (tOffset.top + table.outerHeight() < wH - config.bottomSpace) {
                createRow();
                count++;
                if (config.maxRows && config.maxRows > config.minRows && config.maxRows == count)
                    break;
            }
            while (count < config.minRows) {
                createRow();
                count++;
            }
        }
    }
    initBody();

    function renderDataOnPreparedTable(animate) {
        // page count
        var rows = tbody.find('tr'),
            pageCount = data.length / rows.length;
        if (Math.floor(pageCount) < pageCount)
            pageCount = Math.floor(pageCount) + 1;
        // fix erroneous page number
        if (page > pageCount || page < 1) page = 1;
        // display page number
        if (pager)
            pager.text((pageCount>0?page:0) + '/' + pageCount);
        // start position
        var s = (page - 1) * rows.length;
        // render current page
        rows.each(function(r) {
            var row = $(this);
            if (s + r < data.length) {
                if (pageCount > 1 && animate)
                    row.hide('fast');
                row.find('td').each(function(f) {
                    var text = config.fields[f].formatter(
                        data[s + r][config.fields[f].key]);
                    if (text)
                        $(this).text(text);
                    else
                        $(this).html('&nbsp;');
                });
                if (pageCount > 1 && animate)
                    row.show('fast');
            } else {
                row.find('td').each(function() {
                    $(this).html('&nbsp;');
                });
            }
        });
    }

    function renderDataOnEmptyTable(animate) {
        if (!config.maxRows) return;
        // page count
        var pageCount = data.length / config.maxRows;
        if (Math.floor(pageCount) < pageCount)
            pageCount = Math.floor(pageCount) + 1;
        // fix erroneous page number
        if (page > pageCount || page < 1) page = 1;
        // display page number
        if (pager)
            pager.text((pageCount>0?page:0) + '/' + pageCount);
        // start position
        var s = (page - 1) * config.maxRows,
            c = 0;
        // re-render data of the current page
        tbody.empty();
        while (c < config.maxRows) {
            if (s + c < data.length) {
                var row = createRow();
                if (pageCount > 1 && animate)
                    row.hide('fast');
                row.find('td').each(function(f) {
                    var text = config.fields[f].formatter(
                        data[s + c][config.fields[f].key]);
                    if (text)
                        $(this).text(text);
                    else
                        $(this).html('&nbsp;');
                });
                if (pageCount > 1 && animate)
                    row.show('fast');
            } else if (config.minRows && c < config.minRows) {
                createRow();
            }
            c++;
        }
    }

    function renderData(animate) {
        if (config.autoGrow)
            renderDataOnPreparedTable(animate);
        else
            renderDataOnEmptyTable(animate);
    }

    function loadAll(values) {
        if (Array.isArray ? !Array.isArray(values) : !('length' in values))
            values = [values];
        if (values.length > 0) {
            if ('key' in values[0] && 'data' in values[0] && typeof(values[0].data) == 'object') {
                dataKeys = values.map(function(o) { return o.key; });
                data = values.map(function(o) { return o.data; });
            } else {
                dataKeys = null;
                data = values;
            }
        } else {
            data = [];
            dataKeys = null;
        }
    }

    function updateRow(key, row) {
        if (dataKeys == null) {
            if (data.length == 0) {
                // no data - not sure whether use dataKeys yet
                if (key == 0) { // no
                    data.push(row);
                } else { // yes
                    dataKeys = [key];
                    data.push(row);
                }
            } else {
                // use array index
                if (data[key])
                    data[key] = row;
                else
                    data.push(row);
            }
        } else {
            // use dataKeys
            var i = dataKeys.indexOf(key);
            if (i < 0) {
                // new key
                dataKeys.push(key);
                data.push(row);
            } else {
                // existing key
                data[i] = row;
            }
        }
    }

    function removeRow(key) {
        if (dataKeys == null) {
            // use array index
            if (data[key]) data.splice(key, 1);
        } else {
            // use dataKeys
            var i = dataKeys.indexOf(key);
            if (i >= 0) {
                data.splice(i, 1);
                dataKeys.splice(i, 1);
            }
        }
    }

    $(window).on('resize', function() {
        jobQueue.push(['re-init']);
    });

    // scheduler
    setInterval(function() {
        var job = jobQueue.shift();
        if (!job) return;
        switch (job[0]) {
            case 'load-data':
                loadAll(job[1]);
                renderData();
                break;
            case 'update-row':
                updateRow(job[1], job[2]);
                renderData();
                break;
            case 'remove-row':
                removeRow(job[1]);
                renderData();
                break;
            case 're-init':
                initBody();
                renderData();
                break;
            case 'show-page':
                page = job[1];
                renderData();
                break;
            case 'next-page':
                page++;
                renderData(true);
                break;
        }
    }, 100);

    jobQueue.push(['show-page', 1]);
    setInterval(function() {
        jobQueue.push(['next-page']);
    }, config.pageSpeed);

    return {
        load: function(values) {
            jobQueue.push(['load-data', values]);
        },
        clear: function() {
            jobQueue.push(['load-data', []]);
        },
        update: function(row, values) {
            jobQueue.push(['update-row', row, values]);
        },
        remove: function(row) {
            jobQueue.push(['remove-row', row]);
        }
    };
}
