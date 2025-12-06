FROM ubuntu:24.04@sha256:c920ba4cfca05503764b785c16b76d43c83a6df8d1ab107e7e6610000d94315c

RUN apt-get update && apt-get install -y socat adduser

ENV user je
ENV chall_port 8080

RUN apt-get update && apt-get install -y socat

RUN adduser -u 1337 $user

WORKDIR /home/$user

COPY ./flag ./flag
COPY ./deploy/main ./main

RUN chmod 644 ./flag
RUN chmod 755 ./main

USER $user

EXPOSE $chall_port
CMD socat TCP-LISTEN:8080,reuseaddr,fork EXEC:./main,stderr
