FROM ubuntu:24.04

RUN apt-get -y update --fix-missing
RUN apt-get -y upgrade
RUN apt-get -y update --fix-missing

FROM pwn.red/jail@sha256:ee52ad5fd6cfed7fd8ea30b09792a6656045dd015f9bef4edbbfa2c6e672c28c
COPY --from=0 / /srv
RUN mkdir /srv/app
COPY ./chal /srv/app/run
COPY ./flag.txt /srv/app/

ENV JAIL_MEM=100M JAIL_TIME=300 JAIL_PORT=5011
EXPOSE 5011
