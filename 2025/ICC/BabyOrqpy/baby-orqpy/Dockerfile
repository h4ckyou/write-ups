FROM ubuntu:24.04@sha256:9cbed754112939e914291337b5e554b07ad7c392491dba6daf25eef1332a22e8

RUN apt-get -y update --fix-missing
RUN apt-get -y upgrade
RUN apt-get -y update --fix-missing

FROM pwn.red/jail@sha256:ee52ad5fd6cfed7fd8ea30b09792a6656045dd015f9bef4edbbfa2c6e672c28c
COPY --from=0 / /srv
RUN mkdir /srv/app
COPY ./chal /srv/app/run
COPY ./flag.txt /srv/app/

ENV JAIL_MEM=100M JAIL_TIME=30 JAIL_PORT=5000 JAIL_POW=9000
EXPOSE 5000
