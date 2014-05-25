var SAAgent = null;
var SASocket = null;
var CHANNELID = 104;
var ProviderAppName = "FightProvider";
var accelerationEvents = [];
var fight = false;

function onerror(err) {
	console.log("err [" + err.name + "] msg[" + err.message + "]");
}

var agentCallback = {
	onconnect: function (socket) {
		SASocket = socket;
		alert("HelloAccessory Connection established with RemotePeer");
		SASocket.setSocketStatusListener(function (reason) {
			console.log("Service connection lost, Reason : [" + reason + "]");
			disconnect();
		});
	},
	onerror: onerror
};

var peerAgentFindCallback = {
	onpeeragentfound: function (peerAgent) {
		try {
			if (peerAgent.appName == ProviderAppName) {
				SAAgent.setServiceConnectionListener(agentCallback);
				SAAgent.requestServiceConnection(peerAgent);
			} else {
				alert("Not expected app!! : " + peerAgent.appName);
			}
		} catch (err) {
			console.log("exception [" + err.name + "] msg[" + err.message + "]");
		}
	},
	onerror: onerror
}

function onsuccess(agents) {
	try {
		if (agents.length > 0) {
			SAAgent = agents[0];
			
			SAAgent.setPeerAgentFindListener(peerAgentFindCallback);
			SAAgent.findPeerAgents();
		} else {
			alert("Not found SAAgent!!");
		}
	} catch (err) {
		console.log("exception [" + err.name + "] msg[" + err.message + "]");
	}
}

function connect() {
	if (SASocket) {
		alert('Already connected!');
        return false;
    }
	try {
		webapis.sa.requestSAAgent(onsuccess, onerror);
	} catch(err) {
		console.log("exception [" + err.name + "] msg[" + err.message + "]");
	}
}

function disconnect() {
	try {
		if (SASocket != null) {
			SASocket.close();
			SASocket = null;
			createHTML("closeConnection");
		}
	} catch(err) {
		console.log("exception [" + err.name + "] msg[" + err.message + "]");
	}
}

function onreceive(channelId, data) {
	console.log(data);
	
	if (data.event === 'attack') {
		attackAlert();
	} else if (data.event === 'start') {
		startFight();
	} else if (data.event === 'win') {
		win();
	} else if (data.event === 'loose') {
		loose();
	} else {
		console.log('Unknown event', data.event);
	}
}

function fetch() {
	try {
		SASocket.setDataReceiveListener(onreceive);
		SASocket.sendData(CHANNELID, "Hello Accessory!");
	} catch(err) {
		console.log("exception [" + err.name + "] msg[" + err.message + "]");
	}
}

function attackAlert() {
	navigator.vibrate([1000, 1000, 1000]);
	$('#textbox').html('Э! Пойдем выйдем!');
	$('#textbox').show();
};

function startFight() {
	$('#textbox').html('Бей сильнее!');
	fight = true;
};

function win() {
	$('#textbox').html('Ты выиграл!');
	fight = false;
};

function loose() {
	$('#textbox').html('Ты проиграл!');
	fight = false;
};

function hit(strength) {
	console.log(strength);
}

$(window).load(function(){
	document.addEventListener('tizenhwkey', function(e) {
        if(e.keyName == "back")
            tizen.application.getCurrentApplication().exit();
    });
});

window.addEventListener('devicemotion', function (e) {
	accelerationEvents.push(Math.abs(e.acceleration.x) + Math.abs(e.acceleration.x));
});

setInterval(function () {
	var sum = accelerationEvents.reduce(function (memo, el) {
		return memo + el;
	}, 0);
	
	accelerationEvents = [];
	
	if (sum > 100) {
		hit(sum);
	}
}, 500);