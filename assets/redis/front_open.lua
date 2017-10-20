-- huangxin <3203317@qq.com>

local db        = KEYS[1];
local front_id  = KEYS[2];

local open_time  = ARGV[1];
local front_host = ARGV[2];

-- 

redis.call('SELECT', db);

redis.call('HMSET', 'prop::front::'.. front_id, 'open_time',  open_time,
                                                'host',       front_host,
                                                'user_count', 0);

redis.call('SADD', 'set::front', front_id);

-- 

return 'OK';
