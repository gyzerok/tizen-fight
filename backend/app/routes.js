'use strict';

var AuthController = require('./controllers/AuthController');

module.exports = function (app) {
    app.post('/login', AuthController.login);
};