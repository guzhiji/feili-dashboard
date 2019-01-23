
function testAndGo(url) {
	var xhr = new XMLHttpRequest();
	xhr.open('GET', url);
	xhr.onreadystatechange = function() {
		if (xhr.readyState === 4 && xhr.status === 200) {
			window.location.href = url;
		} else {
			document.getElementById('form-url').setAttribute('style', 'display: block');
			document.getElementById('text-url').value = url;
		}
	};
	xhr.send();
}

document.addEventListener('deviceready', function() {
	if (AndroidFullScreen) AndroidFullScreen.leanMode();

	var url = window.localStorage.getItem('url');
	if (!url) url = 'http://172.20.70.65:58080/shipment';
	if (history.length < 2) {
		testAndGo(url);
	} else {
		document.getElementById('form-url').setAttribute('style', 'display: block');
		document.getElementById('text-url').value = url;
	}

}, false);

document.getElementById('btn-ok').addEventListener('click', function() {
	var url = document.getElementById('text-url').value;
	window.localStorage.setItem('url', url);
	testAndGo(url);
}, false);

