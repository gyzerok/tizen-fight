'use strict';

var AuthController = require('./controllers/AuthController');
var GameController = require('./controllers/GameController');

module.exports = function (app) {
    app.post('/login', AuthController.login);

    app.io.on('connection', function (socket) {
        socket.userId = socket.handshake.decoded_token.userId;
        GameController.onClientConnected(socket);

        socket.on('disconnect', function () {
            GameController.onClientDisconnected(socket);
        });
    });
};