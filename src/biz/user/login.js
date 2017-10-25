/*!
 * g2.biz
 * Copyright(c) 2017 huangxin <3203317@qq.com>
 * MIT Licensed
 */
'use strict';

const path = require('path');
const cwd  = process.cwd();
const conf = require(path.join(cwd, 'settings'));

const uuid  = require('node-uuid');

const md5   = require('speedt-utils').md5;
const utils = require('speedt-utils').utils;

const redis  = require('g2.db').redis;

const biz = require('g2.biz');

const _  = require('underscore');
_.str    = require('underscore.string');
_.mixin(_.str.exports());

(() => {
  /**
   * 用户登陆
   *
   * @return
   */
  exports = module.exports = function(logInfo /* 用户名及密码 */){
    return new Promise((resolve, reject) => {
      biz.user.getByName(logInfo.user_name)
      .then(p1.bind(null, logInfo))
      .then(loginToken.bind(null, logInfo.server_id))
      .then(token => resolve(token))
      .catch(reject);
    });
  };

  function p1(logInfo, user){
    if(!user)             return Promise.reject('用户不存在');
    if(1 !== user.status) return Promise.reject('禁用状态');

    if(md5.hex(user.salt + logInfo.user_pass) !== user.user_pass)
      return Promise.reject('用户名或密码输入错误');

    return Promise.resolve(user);
  }

  function loginToken(server_id, user){
    return new Promise((resolve, reject) => {
      biz.frontend.available(server_id)
      .then(authorize.bind(null, user))
      .then(token => resolve(token))
      .catch(reject);
    });
  };

  var sha1    = 'cdb7efbcf82f489dddec1d38a8f59a22a6e151a5';
  var numkeys = 4;
  var seconds = 5;

  function authorize(user, front_info){
    return new Promise((resolve, reject) => {
      redis.evalsha(
        sha1,
        numkeys,
        conf.redis.database,                   /* */
        conf.app.id,                           /* */
        user.id,                               /* */
        utils.replaceAll(uuid.v4(), '-', ''),  /* */
        seconds,
        front_info[0],
        (err, code) => {
          if(err) return reject(err);
          resolve([code, front_info]);
        });
    });
  }
})();
