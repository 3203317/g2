/*!
 * g2.biz
 * Copyright(c) 2017 huangxin <3203317@qq.com>
 * MIT Licensed
 */
'use strict';

const fs = require('fs');

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

const logger = require('log4js').getLogger('biz');

// (() => {
//   redis.script('load', fs.readFileSync(path.join(cwd, '..', '..', 'assets', 'redis', 'authorize.lua'), 'utf-8'), (err, sha1) => {
//     if(err) return process.exit(1);
//     logger.info('sha1 authorize: %j', sha1);
//   });
// })();

(() => {
  /**
   * 后置机登陆
   *
   * @return
   */
  exports = module.exports = function(front_id /* 前置机id */, user_id /* 后置机id */, chan_type){
    return new Promise((resolve, reject) => {
      redis.evalsha(
        sha1,
        numkeys,
        conf.redis.database,                   /* */
        conf.app.id,                           /* */
        user_id,                               /* */
        utils.replaceAll(uuid.v4(), '-', ''),  /* */
        seconds,
        front_id,
        chan_type || CHAN_TYPE,
        (err, code) => {
          if(err) return reject(err);
          resolve(code);
        });
    });
  };

  var sha1      = process.env.BIZ_BACKEND_LOGIN_SHA1 || 'f4e4f49554a6781f0e45f1e404bbc2f2c0129005';
  var numkeys   = 4;
  var seconds   = 5;
  var CHAN_TYPE = 'USER';
})();
