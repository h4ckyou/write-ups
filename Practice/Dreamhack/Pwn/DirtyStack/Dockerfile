FROM ubuntu:18.04

RUN apt-get update -y
RUN apt-get install xinetd -y
RUN useradd dirty_stack

ADD ./share /home/dirty_stack

RUN echo "#!/bin/bash\ntimeout 60 /home/dirty_stack/dirtystack" > /home/dirty_stack/run.sh

RUN chown -R root:dirty_stack /home/dirty_stack
RUN chmod 750 /home/dirty_stack
RUN chmod 750 /home/dirty_stack/run.sh
RUN chmod 750 /home/dirty_stack/dirtystack

COPY xinetd /etc/xinetd.d/prob
CMD ["/usr/sbin/xinetd","-dontfork"]
