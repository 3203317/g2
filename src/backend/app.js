/*!
 * g2.backend
 * Copyright(c) 2016 huangxin <3203317@qq.com>
 * MIT Licensed
 */
'use strict';

const fs = require('fs'),
      path = require('path'),
      cwd = process.cwd();

const utils = require('speedt-utils').utils;

const conf = require('./settings');

const log4js = require('log4js');

log4js.configure({
  appenders: {
    app: {
      type: 'dateFile',
      filename: path.join(cwd, 'logs', 'app'),
      pattern: '.yyyy-MM-dd.log',
      alwaysIncludePattern: true
    },
    console: {
      type: 'console'
    }
  },
  categories: {
    default: {
      appenders: ['app', 'console'],
      level: 'debug'
    }
  }
});

const logger = log4js.getLogger('app');

logger.info('server started: %j', conf.app.id);

process.on('uncaughtException', err => {
  logger.error('uncaughtException:', err);
});

function exit(){
  zkCli.close();
  process.exit(0);
}

process.on('SIGINT',  exit);
process.on('SIGTERM', exit);

const zookeeper = require('node-zookeeper-client'),
      zkCli = zookeeper.createClient(conf.zookeeper.host, conf.zookeeper.options);

zkCli.once('connected', () => {
  zkCli.create(
    '/fishjoy/backend/'+ conf.app.id,
    new Buffer(JSON.stringify(conf)),
    zookeeper.CreateMode.EPHEMERAL,
    (err, path) => {
      if(err){
        logger.error(err);
        return exit();
      }

      logger.info('zkNode created: %j', path);
    });
});

zkCli.connect();


const WebSocket = require('ws');
const ws = new WebSocket('ws://127.0.0.1:12988');

ws.on('open', function open(){
  ws.send(Buffer.from('123', 'utf8'));
});
