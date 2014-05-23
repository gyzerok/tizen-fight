( function () {
	window.addEventListener( 'tizenhwkey', function( ev ) {
		if( ev.keyName == "back" ) {
			var page = document.getElementsByClassName( 'ui-page-active' )[0],
				pageid = page ? page.id : "";
			if( pageid === "main" ) {
				tizen.application.getCurrentApplication().exit();
			} else {
				window.history.back();
			}
		}
	} );
	var motionManager = window.webapis.motion;
	var tmp = window.getElementById('id');
	motionManager.getMotionInfo('WRIST_UP', function (data) {
		for (var item in data) tmp.innerHTML(tmp.innerHTML() + ' ' + data);
	}, function (err) {
		tmp.innerHTML('error!');
	});
} () );
