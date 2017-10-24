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
exports = module.exports = function(){
  return new Promise((resolve, reject) => {
    resolve(['127.0.0.1', '68']);
  });
};
