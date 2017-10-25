-- huangxin <3203317@qq.com>

local db        = KEYS[1];
local client_id = KEYS[2];
local user_id   = KEYS[3];

redis.call('SELECT', db);

-- 

local  _key = client_id ..'::'.. user_id;

local _code = redis.call('GET', _key);
if   (_code) then return _code; end;

-- 

local code     = KEYS[4];
local seconds  = ARGV[1];
local front_id = ARGV[2];

redis.call('SET',    _key, code);
redis.call('EXPIRE', _key, seconds);

--[[
{
  code: {
    client_id: '',
    id:        '',
    front_id:  '',
  }
}
--]]
redis.call('HMSET', code, 'client_id', client_id,
                          'id',        user_id,
                          'front_id',  front_id);

redis.call('EXPIRE', code, seconds);

return code;
