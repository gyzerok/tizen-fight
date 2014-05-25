var app = require('http').createServer(handler);
var io = require('socket.io').listen(app);
var fs = require('fs');
var users = {};

io.set('log level', 1);

function handler(req, res) {
  fs.readFile(__dirname + '/index.html', function (err, data) {
    if (err) {
      res.writeHead(500);
      return res.end('Error loading index.html');
    }

    res.writeHead(200);
    res.end(data);
  });
}

function updateUserList(users) {
  for (var user in users) {
    users[user].socket.emit('user-list', Object.keys(users).filter(function (u) { return u !== user; }));
  }
}

function createFight(sourceUser, targetUser) {
  users[sourceUser].fight = {
    user: targetUser,
    health: 100
  };

  users[targetUser].fight = {
    user: sourceUser,
    health: 100
  };

  users[targetUser].socket.emit('new-fight');
  users[sourceUser].socket.emit('new-fight');
  users[targetUser].socket.emit('your-health', 100);
  users[targetUser].socket.emit('enemy-health', 100);
  users[sourceUser].socket.emit('your-health', 100);
  users[sourceUser].socket.emit('enemy-health', 100);
}

io.sockets.on('connection', function (socket) {
  var username;

  socket.on('handshake', function (data) {
    username = data.username;
    users[username] = { socket: socket };
    updateUserList(users);

    console.log('User ' + username + ' joined the room');
    console.log('User list: ', Object.keys(users));
  });

  socket.on('disconnect', function () {
    delete users[username];
    updateUserList(users);
  });

  socket.on('fight-request', function (user) {
    createFight(user, username);
  });

  socket.on('hit', function (data) {
    if (users[username].fight) {
      var targetUser = users[username].fight.user;
      users[targetUser].fight.health -= data / 1000;

      if (users[targetUser].fight.health > 0) {
        users[targetUser].socket.emit('your-health', users[targetUser].fight.health);
        users[username].socket.emit('enemy-health', users[targetUser].fight.health);
      } else {
        users[targetUser].socket.emit('loose');
        users[username].socket.emit('win');
        delete users[targetUser].fight;
        delete users[username].fight;
      }
    }
  });
});

app.listen(Number(process.env.PORT || 8080));
