'use strict';

module.exports = function (app) {
    return function (id) {
        var _ = require('underscore');
        return _.filter(app.io.sockets, function (socket) {
            return socket.handshake.decoded_token.userId == id;
        })[0];
    };
};