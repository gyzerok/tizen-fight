var file = new (require('node-static').Server)('./client');
var http = require('http');
var app = http.createServer(function (req, res) {
    file.serve(req, res);
}).listen(1234);

var io = require('socket.io').listen(app);

io.sockets.on('connection', function (socket) {

    socket.on('message', function (message) {
        console.log(message);
        socket.broadcast.emit('message', message);
    });

    socket.on('create or join', function (room) {
        var numClients = io.sockets.clients(room).length;

        if (numClients == 0) {
            socket.join(room);
            socket.emit('created', room);
            console.log('first client');
        }
        else if (numClients == 1) {
            io.sockets.in(room).emit('join', room);
            socket.join(room);
            socket.emit('joined', room);
            console.log('second client');
        }
        else {
            console.log('max clients');
            socket.emit('full', room);
        }

        socket.emit('emit(): client ' + socket.id + ' joined room ' + room);
        socket.broadcast.emit('broadcast(): client ' + socket.id + ' joined room ' + room);
    });
});