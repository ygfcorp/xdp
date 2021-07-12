FROM amd64/adoptopenjdk:11.0.9.1_1-jdk-hotspot
USER root

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en' LC_ALL='en_US.UTF-8'
ENV TZ Asia/Seoul

RUN apt update
RUN apt upgrade -y

RUN mkdir -p /xdp/apps
RUN mkdir -p /xdp/pkgs
RUN mkdir -p /xdp/logs

RUN useradd --user-group --create-home --shell /bin/false xdp
RUN usermod --shell /sbin/nologin nobody
RUN chown -R xdp:xdp /xdp

