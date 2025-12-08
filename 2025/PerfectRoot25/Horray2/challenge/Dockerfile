FROM ubuntu:latest
RUN useradd -d /home/user -m -p user -s /bin/bash user
RUN echo "user:user" | chpasswd
RUN mkdir -p /home/user/pwn
WORKDIR /home/user/pwn
COPY . .
COPY socat /usr/bin/
RUN cat /dev/urandom | head -c 32 > /tmp/x
RUN echo "flag{$(md5sum /tmp/x|cut -d ' ' -f1)}" > flag.txt
RUN chmod +x heap2
EXPOSE 5252
USER user
#CMD socat tcp-l:5252,reuseaddr,fork EXEC:"./heap2",pty,stderr,setsid,sigint,sighup,echo=0,sane,raw,ignbrk=1
CMD socat TCP-LISTEN:5252,reuseaddr,fork EXEC:"stdbuf -i0 -o0 -e0 ./heap2 && /bin/bash"

