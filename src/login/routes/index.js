/*!
 * g2.login
 * Copyright(c) 2016 huangxin <3203317@qq.com>
 * MIT Licensed
 */
'use strict';

const test = require('../controllers/test');
const user = require('../controllers/user');

module.exports = function(app){
  app.post('/user/login$', user.login);
  app.get ('/user/login$', user.loginUI);

  app.get('/', test.indexUI);
};
