#! /bin/bash


docker build -t heap3_chall ./
docker stop heap3 2>/dev/null
docker rm heap3 2>/dev/null
docker run -itd --name heap3 --user user heap3_chall
docker inspect heap3 | grep -i IPAddress