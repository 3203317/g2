#!/bin/bash

echo ""
echo "front_open.lua"
/root/my/redis/redis-3.2.6/src/redis-cli -a 123456 script load "$(cat /root/my/git/3203317/g2/assets/redis/front_open.lua)"

echo ""
echo "front_close.lua"
/root/my/redis/redis-3.2.6/src/redis-cli -a 123456 script load "$(cat /root/my/git/3203317/g2/assets/redis/front_close.lua)"

echo ""
echo "front_blacklist.lua"
/root/my/redis/redis-3.2.6/src/redis-cli -a 123456 script load "$(cat /root/my/git/3203317/g2/assets/redis/front_blacklist.lua)"

echo ""
echo "token.lua"
/root/my/redis/redis-3.2.6/src/redis-cli -a 123456 script load "$(cat /root/my/git/3203317/g2/assets/redis/token.lua)"
