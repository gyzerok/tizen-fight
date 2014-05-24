'use strict';

var express = require('express');
var app = express();
var server = require('http').Server(app);
var io = require('socket.io').listen(server);

// Setting up configuration
require('./config')(app);
io.set('log level', app.get('log-level'));

var socketJwt = require('socketio-jwt');
io.set('authorization', socketJwt.authorize({
    secret: app.get('secret'),
    handshake: true
}));

app.io = io.sockets;
app.io.get = require('./sockByUserId')(app);
app.use(require('body-parser')());

// Setting up routes
require('./routes')(app);

module.exports = {
    server: server,
    app: app
};