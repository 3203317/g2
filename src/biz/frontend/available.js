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

/**
 *
 * @return
 */
exports = module.exports = function(front_id /* 前置机id */, user_id /* 后置机id */){
  return new Promise((resolve, reject) => {
    resolve(['68', '127.0.0.1:12988']);
  });
};
