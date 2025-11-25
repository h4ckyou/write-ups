FROM ubuntu:24.04@sha256:04f510bf1f2528604dc2ff46b517dbdbb85c262d62eacc4aa4d3629783036096 AS base
RUN apt-get update && apt-get install libstdc++6
WORKDIR /app
ADD --chmod=555 run .
ADD --chmod=555 hoard .
ADD --chmod=555 libhoard.so .
ADD --chmod=444 flag.txt /flag.txt
RUN mv /flag.txt /flag-$(md5sum /flag.txt | awk '{print $1}').txt

FROM pwn.red/jail
COPY --from=base / /srv
ENV JAIL_TIME=300 JAIL_CPU=100 JAIL_MEM=10M
