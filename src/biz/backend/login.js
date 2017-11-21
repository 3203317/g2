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
   * 后置机登陆
   *
   * @return
   */
  exports = module.exports = function(logInfo /* 后置机信息 */){
    return authorize(logInfo);
  };

  var sha1    = 'cdb7efbcf82f489dddec1d38a8f59a22a6e151a5';
  var numkeys = 4;
  var seconds = 5;

  function authorize(user){
    return new Promise((resolve, reject) => {
      redis.evalsha(
        sha1,
        numkeys,
        conf.redis.database,                   /* */
        conf.app.id,                           /* */
        user.id,                               /* */
        utils.replaceAll(uuid.v4(), '-', ''),  /* */
        seconds,
        user.front_id,
        user.type,
        (err, code) => {
          if(err) return reject(err);
          resolve(code);
        });
    });
  }
})();
