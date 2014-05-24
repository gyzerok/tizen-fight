'use strict';

var server = require('./app/app').server;
var app = require('./app/app').app;

server.listen(app.get('port'), function () {
    console.log('Server running on ' + server.address().address + ':' + server.address().port);
});