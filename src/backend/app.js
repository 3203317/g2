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

process.on('uncaughtException', err => {
  logger.error('uncaughtException:', err);
});

process.on('exit', code => { logger.info('exit code: %j', code) });

(() => {
  function exit(){ process.exit() }
  process.on('SIGINT',  exit);
  process.on('SIGTERM', exit);
})();

logger.info('server started: %j', conf.id);

(() => {
  process.on('exit', code => {
    if(zkCli && zkCli.close) zkCli.close();
  });

  const zookeeper = require('node-zookeeper-client'),
        zkCli = zookeeper.createClient(conf.zookeeper.host, conf.zookeeper.options);

  zkCli.once('connected', () => {
    logger.info('zk connected: %j', conf.zookeeper.host);

    zkCli.create(
      '/fishjoy/back/'+ conf.id,
      Buffer.from(JSON.stringify(conf)),
      zookeeper.CreateMode.EPHEMERAL,
      (err, path) => {
        if(err){
          logger.error(err);
          return process.exit(1);
        }

        logger.info('zk node created: %j', path);
      });
  });

  zkCli.connect();
})();

const biz = require('g2.biz');

biz.backend.login(conf.front.id, conf.id, 'BACK')
.then(conn)
.catch(err => {
  logger.error(err);
  process.exit(1);
});

function conn(code){
  const WebSocket = require('ws'),
        ws = new WebSocket('ws://'+ conf.front.host);

  process.on('exit', code => {
    if(ws && ws.close) ws.close();
  });

  ws.on('open', function open(){
    ws.send(Buffer.from(code, 'utf8'));

    setTimeout(function(){
      var p7 = ['', 7, ''];
      ws.send(Buffer.from(JSON.stringify(p7), 'utf8'));

      var p2 = ['', 2, ''];
      ws.send(Buffer.from(JSON.stringify(p2), 'utf8'));
    }, 1000);
  });

  ws.on('error', err => {
    logger.error(err);
    process.exit(1);
  });

  ws.on('close', function close(){
    logger.info('ws closed');
    process.exit();
  });

  ws.on('message', function incoming(data){
    var arr = JSON.parse(data.toString());
    logger.debug('method: %s, data: %s', arr[0], arr[1])
  });
}
