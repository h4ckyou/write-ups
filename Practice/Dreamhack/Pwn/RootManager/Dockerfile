FROM ubuntu:24.04

RUN apt update
RUN apt-get install -y socat

RUN useradd -m -s /bin/bash pwn

RUN apt-get update && \
    apt-get install -y build-essential wget libpam0g-dev libselinux1-dev zlib1g-dev \
                       pkg-config libssl-dev git ca-certificates && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /opt
RUN wget https://www.sudo.ws/dist/sudo-1.9.16p2.tar.gz && \
    tar xzf sudo-1.9.16p2.tar.gz && \
    cd sudo-1.9.16p2 && \
    ./configure --disable-gcrypt --prefix=/usr && make && make install

WORKDIR /home/pwn

COPY ./flag ./flag
COPY ./note_manager ./note_manager

RUN chmod 400 ./flag 
RUN chmod 755 ./note_manager
RUN chown root:root ./flag

USER pwn

EXPOSE 1557
CMD socat TCP-LISTEN:1557,reuseaddr,fork EXEC:./note_manager,stderr
