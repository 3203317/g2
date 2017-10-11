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
  var sql = 'SELECT a.* FROM s_user a WHERE a.user_name=?';

  /**
   * 获取用户
   *
   * @return
   */
  exports = module.exports = function(user_name, trans){
    return Promise.resolve({
      id:        '123456',
      user_name: 'hx',
      user_pass: 'e10adc3949ba59abbe56e057f20f883e',
      status:    1,
    });
  };
})();
