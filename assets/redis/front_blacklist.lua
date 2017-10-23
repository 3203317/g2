-- huangxin <3203317@qq.com>

local db        = KEYS[1];
local client_ip = KEYS[2];

-- 

redis.call('SELECT', db);

-- 

return 'OK';
