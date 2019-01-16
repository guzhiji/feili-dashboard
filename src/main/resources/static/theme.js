var theme = (function() {

    var registry = [];

    function getTheme(board) {
        if (window.localStorage)
            return window.localStorage.getItem(board + '_theme');
        return null;
    }

    function setTheme(board, name) {
        if (!window.localStorage)
            return;
        for (var i = 0; i < registry.length; i++) {
            if (registry[i].name == name) {
                window.localStorage.setItem(board + '_theme', name);
                break;
            }
        }
    }

    function register(name, displayName, activationFunc) {
        registry.push({
            name: name,
            displayName: displayName,
            activationFunc: activationFunc
        });
    }

    function hideMenu() {
        $('#context-menu').css('display', 'none');
    }

    function changeThemeFunc(board, theme) {
        return function() {
            theme.activationFunc();
            setTheme(board, theme.name);
            hideMenu();
            return false;
        };
    }

    function init(board) {
        if ($('#context-menu').length)
            return;
        var menu = $('<ul class="dropdown-menu" id="context-menu"></ul>'),
            curTheme = getTheme(board),
            themeApplied = false,
            menuItem;
        for (var i in registry) {
            // create menu button for the theme
            menuItem = $('<a href="#"></a>');
            menuItem
                .attr('id', 'menu-theme-' + registry[i].name)
                .text(registry[i].displayName)
                .on('click', changeThemeFunc(board, registry[i]));
            menu.append($('<li></li>').append(menuItem));
            // apply theme
            if (!themeApplied) {
                if (curTheme == registry[i].name || !curTheme) {
                    registry[i].activationFunc();
                    themeApplied = true;
                }
            }
        }
        // divider
        menu.append('<li role="separator" class="divider"></li>');
        // refresh button
        menuItem = $('<a href="#" id="menu-refresh">刷新</a>');
        menuItem.on('click', function() {
            window.location.reload();
            return false;
        });
        menu.append($('<li></li>').append(menuItem));
        // fullscreen button
        /*
        menuItem = $('<a href="#" id="menu-fullscreen">全屏</a>');
        menuItem.on('click', function() {
            requestFullScreen(document.body);
            hideMenu();
            return false;
        });
        menu.append($('<li></li>').append(menuItem));
        */
        // install the menu
        $('body')
            .on('click', hideMenu)
            .on('contextmenu', function(e) {
                $('#context-menu').css({
                    display: 'block',
                    left: e.pageX,
                    top: e.pageY
                });
                return false;
            })
            .append(menu);
    }

    return {
        hideMenu: hideMenu,
        register: register,
        init: init
    };
})();