FROM ubuntu:latest
RUN useradd -d /home/user -m -p user -s /bin/bash user
RUN echo "user:user" | chpasswd
RUN mkdir -p /home/user/ctf
WORKDIR /home/user/ctf
COPY . .
COPY socat /usr/bin/
RUN cat /dev/urandom | head -c 32 > /tmp/x
RUN echo "flag{$(md5sum /tmp/x|cut -d ' ' -f1)}" > flag.txt
RUN chmod +x heap2
EXPOSE 5151
USER user
CMD socat TCP-LISTEN:5151,reuseaddr,fork EXEC:"stdbuf -i0 -o0 -e0 ./heap2 && /bin/bash"

