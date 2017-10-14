#!/bin/bash

echo ""
echo "front_open.lua"
/root/my/redis/redis-3.2.6/src/redis-cli -a 123456 -p 12379 script load "$(cat /root/my/git/3203317/g2/assets/redis/front_open.lua)"

echo ""
echo "front_close.lua"
/root/my/redis/redis-3.2.6/src/redis-cli -a 123456 -p 12379 script load "$(cat /root/my/git/3203317/g2/assets/redis/front_close.lua)"
