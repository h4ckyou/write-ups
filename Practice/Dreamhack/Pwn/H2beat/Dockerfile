FROM ubuntu:20.04

RUN apt-get update && apt-get install -y socat
RUN useradd -mU ctf

ADD ./deploy/prob /home/ctf
ADD ./deploy/flag /home/ctf

RUN chown -R root:ctf /home/ctf
RUN chmod -R 750 /home/ctf

WORKDIR /home/ctf

EXPOSE 10912
CMD socat TCP-LISTEN:10912,reuseaddr,fork EXEC:/home/ctf/prob,su=ctf

