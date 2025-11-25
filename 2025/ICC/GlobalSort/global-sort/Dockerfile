FROM ubuntu:24.04@sha256:72297848456d5d37d1262630108ab308d3e9ec7ed1c3286a32fe09856619a782

RUN apt-get -y update --fix-missing
RUN apt-get -y upgrade
RUN apt-get -y update --fix-missing
RUN apt-get install locales

COPY ./locale.gen /etc/locale.gen
RUN locale-gen

FROM pwn.red/jail@sha256:ee52ad5fd6cfed7fd8ea30b09792a6656045dd015f9bef4edbbfa2c6e672c28c
COPY --from=0 / /srv

RUN mkdir /srv/app
COPY ./chal /srv/app/run

RUN chmod 555 /srv/app/run

WORKDIR /srv/app

ENV JAIL_MEM=20M JAIL_TIME=300 JAIL_PORT=3131
EXPOSE 3131
