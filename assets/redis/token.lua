-- huangxin <3203317@qq.com>

local db         = KEYS[1];
local front_id   = KEYS[2];
local channel_id = KEYS[3];
local code       = KEYS[4];

local seconds    = ARGV[1];
local open_time  = ARGV[2];

redis.call('SELECT', db);

-- 

local _user_id = redis.call('HGET', code, 'id');

if (false == _user_id) then return 'invalid_code'; end;

local _front_id = redis.call('HGET', code, 'front_id');

if (front_id ~= _front_id) then return 'invalid_code'; end;

-- 

local client_id = redis.call('HGET', code, 'client_id');

redis.call('DEL', client_id ..'::'.. _user_id);

-- 

local result = 'OK';

-- 

local s = redis.call('HGET', 'prop::user::'.. _user_id, 'front_id');

if (s) then
  local b = redis.call('HGET', 'prop::user::'.. _user_id, 'channel_id');
  result = s ..'::'.. b;
  redis.call('DEL', result);
end;

-- 重命名

redis.call('RENAME', code, 'prop::user::'.. _user_id);

-- 给当前用户（会话）增加新属性

redis.call('HMSET',  'prop::user::'.. _user_id, 'channel_id', channel_id,
                                               'open_time',  open_time);
redis.call('EXPIRE', 'prop::user::'.. _user_id, seconds);

redis.call('SET',    front_id ..'::'.. channel_id, _user_id);
redis.call('EXPIRE', front_id ..'::'.. channel_id, seconds);

-- 属性::系统::在线人数+1
redis.call('HINCRBY', 'prop::sys', 'online_count', 1);

-- 属性::前置机::在线人数+1
redis.call('HINCRBY', 'prop::front::'.. front_id, 'online_count', 1);

return result;
