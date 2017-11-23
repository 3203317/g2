#!/bin/bash

echo ""
echo "front_blacklist.lua"
/root/my/redis/redis-3.2.6/src/redis-cli -a 123456 script load "$(cat /root/my/git/3203317/g2/assets/redis/front_blacklist.lua)"

echo ""
echo "authorize.lua"
/root/my/redis/redis-3.2.6/src/redis-cli -a 123456 script load "$(cat /root/my/git/3203317/g2/assets/redis/authorize.lua)"

echo ""
echo "token.lua"
/root/my/redis/redis-3.2.6/src/redis-cli -a 123456 script load "$(cat /root/my/git/3203317/g2/assets/redis/token.lua)"
