FROM ubuntu:24.04

ARG USERNAME=app
ENV user=${USERNAME}
ENV chall_port=5000
ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update \
 && apt-get install -y --no-install-recommends socat ca-certificates passwd ca-certificates \
 && rm -rf /var/lib/apt/lists/*

RUN useradd -m -s /bin/sh "${user}" || (echo "useradd failed" && exit 1)

WORKDIR /home/${user}

COPY nolibc /home/${user}/${user}
COPY flag /home/app/flag

RUN chmod 0755 /home/${user}/${user} \
 && chmod 0644 /home/app/flag \
 && chown -R "${user}":"${user}" /home/"${user}" /home/"${user}"/"${user}" || true

EXPOSE ${chall_port}

USER ${user}

CMD sh -c 'exec socat TCP-LISTEN:$chall_port,reuseaddr,fork EXEC:/home/$user/$user,pty,raw,echo=0,stderr,setsid'
