/*!
 * g2.login
 * Copyright(c) 2016 huangxin <3203317@qq.com>
 * MIT Licensed
 */
'use strict';

module.exports = {
  port: 8888,
  app: {
    name: process.env.APP_NAME,
    id: process.env.APP_ID || '1',
  },
  anysdk: {
    private_key:  process.env.ANYSDK_PRIVATE_KEY  || 'E2D5511AFC845DDF8CE220ACE2A0A1C9',
    enhanced_key: process.env.ANYSDK_ENHANCED_KEY || 'OWE2ZmMyOGVmMWNhYzc0MmYyOWU',
  },
  cookie: {
    key: 'web',
    secret: 'login'
  },
  html: {
    virtualPath: '/client/',
    cdn: process.env.HTML_CDN,
  },
  activemq: {
    host: process.env.ACTIVEMQ_HOST || '127.0.0.1',
    port: process.env.ACTIVEMQ_PORT || 12613,
    user: 'admin',
    password: process.env.ACTIVEMQ_PASS || 'admin',
  },
  mysql: {
    database: 'emag3',
    host: process.env.MYSQL_HOST || '127.0.0.1',
    port: process.env.MYSQL_PORT || 12306,
    user: 'root',
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