FROM ubuntu:24.04

ENV USER rootsquare

USER root
RUN apt-get update && apt-get install -y socat python3 python3-pip
RUN adduser -u 1337 $USER

ADD ./deploy /chall

RUN chown -R $USER:$USER /chall
RUN chmod -R 0400 /chall
RUN chmod 0700 /chall
RUN chmod 0700 /chall/input
RUN chmod 0700 /chall/output
RUN chmod 0500 /chall/judge
RUN chmod 0500 /chall/server.py

WORKDIR /chall

USER $USER

EXPOSE 5000

CMD ["socat", "TCP-LISTEN:5000,reuseaddr,fork", "EXEC:python3 server.py,nofork,stderr"]