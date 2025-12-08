#! /bin/bash


docker build -t heap2_chall ./
docker stop heap2 2>/dev/null
docker rm heap2 2>/dev/null
docker run --restart always -itd --name heap2 --user user heap2_chall
docker inspect heap2 | grep -i IPAddress
