FROM python:3.12-slim@sha256:e97cf9a2e84d604941d9902f00616db7466ff302af4b1c3c67fb7c522efa8ed9 AS builder


RUN apt-get update && apt-get install -y clang binutils && rm -rf /var/lib/apt/lists/*

RUN pip install --no-cache-dir z3-solver lark

RUN mkdir -p /app
WORKDIR /app

COPY main.py /app/run
COPY main.py /app/

RUN chmod +x /app/run
COPY parse.py verify.py vast.py executor.py /app

COPY ./flag.txt /app/

FROM pwn.red/jail
COPY --from=builder / /srv

ENV JAIL_MEM=2000M JAIL_POW=5000 JAIL_PORT=12100 JAIL_PIDS=7 JAIL_TMP_SIZE=10M JAIL_TIME=30 JAIL_ENV_PATH="/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin"

EXPOSE 12100
