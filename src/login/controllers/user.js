/*!
 * emag.login
 * Copyright(c) 2016 huangxin <3203317@qq.com>
 * MIT Licensed
 */
'use strict';

const biz = require('emag.biz');

const conf  = require('../settings');
const utils = require('speedt-utils').utils;

const request = require('request');

const logger = require('log4js').getLogger('controllers.user');

exports.loginUI = function(req, res, next){
  res.render('user/login', {
    conf: conf,
    data: {}
  });
};

(() => {
  function p1(res, token){
    res.send({ data: token });
  }

  function p2(res, next, err){
    if('string' === typeof err)
      return res.send({ error: { code: err } });
    next(err);
  }

  exports.login = function(req, res, next){
    var query = req.body;

    biz.user.login(query)
    .then (p1.bind(null, res))
    .catch(p2.bind(null, res, next));
  };
})();
