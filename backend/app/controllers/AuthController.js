'use strict';

module.exports = {
    login: function (req, res) {
        var app = require('../app').app;
        var jwt = require('jsonwebtoken');

        var token = jwt.sign({userId: req.body.nickname}, app.get('secret'), {expiresInMinutes: 60 * 24});

        res.json({token: token});
    }
};