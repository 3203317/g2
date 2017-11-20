/*!
 * g2.backend
 * Copyright(c) 2016 huangxin <3203317@qq.com>
 * MIT Licensed
 */
'use strict';

module.exports = {
  app: {
    id: process.env.SERVER_ID || '1',
  },
  zookeeper: {
    host: process.env.ZOOKEEPER_HOST || '127.0.0.1:12181',
    options: {
      sessionTimeout: process.env.ZOOKEEPER_SESSIONTIMEOUT || (1000 * 30),
      spinDelay: process.env.ZOOKEEPER_SPINDELAY || (1000 * 1),
      retries: process.env.ZOOKEEPER_RETRIES || 0,
    }
  },
  activemq: {
    host: process.env.ACTIVEMQ_HOST || '127.0.0.1',
    port: process.env.ACTIVEMQ_PORT || 12613,
    user: process.env.ACTIVEMQ_USER || 'admin',
    password: process.env.ACTIVEMQ_PASS || 'admin',
  },
  mysql: {
    database: process.env.MYSQL_DB || 'emag3',
    host: process.env.MYSQL_HOST || '127.0.0.1',
    port: process.env.MYSQL_PORT || 12306,
    user: process.env.MYSQL_USER || 'root',
    password: process.env.MYSQL_PASS || 'password',
    connectionLimit: 50
  },
  redis: {
    host: process.env.REDIS_HOST || '127.0.0.1',
    port: process.env.REDIS_PORT || 12379,
    password: process.env.REDIS_PASS || '123456',
    database: 1
  }
};