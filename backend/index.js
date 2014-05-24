var app = require('http').createServer(handler);
var io = require('socket.io').listen(app);
var fs = require('fs');

app.listen(8080);

function handler (req, res) {
  fs.readFile(__dirname + '/index.html',
    function (err, data) {
      if (err) {
        res.writeHead(500);
        return res.end('Error loading index.html');
      }

      res.writeHead(200);
      res.end(data);
    });
}

var users = {};

io.sockets.on('connection', function (socket) {
  var username;

  socket.on('handshake', function (data) {
    username = data.username;
    users[username] = socket;
    for (var user in users) {
      users[user].emit('user-list', Object.keys(users));
    }
  });

  socket.on('disconnect', function () {
    delete users[username];
    for (var user in users) {
      users[user].emit('user-list', Object.keys(users));
    }
  });

  socket.on('fight-request', function (user) {
    console.log(username + ' requests a fight with ' + user);
  });
});
