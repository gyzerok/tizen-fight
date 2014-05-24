'use strict';

module.exports = {
    onClientConnected: function (socket) {
        socket.broadcast.emit('new-client', {nickname: socket.userId});
    },

    onClientDisconnected: function (socket) {
        socket.broadcast.emit('rem-client', {nickname: socket.userId});
    }
};