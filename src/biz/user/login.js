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
      .then(loginToken)
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

  function loginToken(user){
    return Promise.resolve([utils.replaceAll(uuid.v4(), '-', ''), '68']);
  };
})();
