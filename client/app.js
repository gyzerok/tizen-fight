(function () {
    // init
    var PeerConnection = window.mozRTCPeerConnection || window.webkitRTCPeerConnection;
    var IceCandidate = window.mozRTCIceCandidate || window.RTCIceCandidate;
    var SessionDescription = window.mozRTCSessionDescription || window.RTCSessionDescription;
    navigator.getUserMedia = navigator.getUserMedia || navigator.mozGetUserMedia || navigator.webkitGetUserMedia;
    var localVideo = document.querySelector('#video-local');
    var remoteVideo = document.querySelector('#video-remote');
    var pc = new PeerConnection(null);

    // local stream
    navigator.getUserMedia({ audio: true, video: true }, function (stream) {
        localVideo.src = URL.createObjectURL(stream);

        pc.addStream(stream);
        pc.onicecandidate = function (e) {
            console.log('on candidate', e);
            if (e.candidate) {
                sm.sendMessage({
                    type: 'candidate',
                    label: e.candidate.sdpMLineIndex,
                    id: e.candidate.sdpMid,
                    candidate: e.candidate.candidate
                });
            }
        };

        pc.onaddstream = function (e) {
            console.log('on add stream');
            remoteVideo.src = URL.createObjectURL(e.stream);
        };

    }, function (err) {
        console.log(err);
    });

    function createOffer() {
        pc.createOffer(
            function (desc) {
                pc.setLocalDescription(desc);
                sm.sendMessage(desc);
            },
            function (error) {
                console.log(error)
            },
            { 'mandatory': { 'OfferToReceiveAudio': true, 'OfferToReceiveVideo': true } }
        );
    }


    setTimeout(createOffer, 7000);


    var SocketManager = function () {
        this.socket = io.connect('', {port: 1234});
        this.socket.on('message', function (msg) {
            console.log('recive message');
            console.log(msg);
            if (msg.type == 'candidate') {
                var candidate = new IceCandidate({sdpMLineIndex: msg.label, candidate: msg.candidate});
                pc.addIceCandidate(candidate);
            }

            if (msg.type === 'offer') {
                pc.setRemoteDescription(new SessionDescription(msg));
                pc.createAnswer(
                    function (desc) {
                        pc.setLocalDescription(desc);
                        sm.sendMessage(desc);
                    },
                    function (error) {
                        console.log(error)
                    },
                    { 'mandatory': { 'OfferToReceiveAudio': true, 'OfferToReceiveVideo': true } }
                );
            }
            if (msg.type === 'answer') {
                pc.setRemoteDescription(new SessionDescription(msg));
            }
        });
    };

    //sockets
    SocketManager.prototype.sendMessage = function (msg) {
        this.socket.emit('message', msg);
        console.log('sending message...');
        console.log(msg);
    };
    var sm = new SocketManager();
})();