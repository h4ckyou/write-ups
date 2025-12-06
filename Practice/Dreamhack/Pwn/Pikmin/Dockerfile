FROM ubuntu:24.04

ENV USER rootsquare

USER root
RUN apt-get update && apt-get install -y socat
RUN useradd -u 1337 $USER

WORKDIR /chall

COPY pikmin .
COPY flag.txt .

RUN chown $USER:$USER /chall/pikmin /chall/flag.txt
RUN chmod 0500 /chall/pikmin
RUN chmod 0400 /chall/flag.txt

USER $USER

EXPOSE 5000

CMD ["socat", "TCP-LISTEN:5000,reuseaddr,fork", "EXEC:./pikmin,nofork,stderr"]