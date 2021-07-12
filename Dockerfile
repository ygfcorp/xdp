FROM 175329446102.dkr.ecr.ap-northeast-2.amazonaws.com/rptf-base:1.0.0
USER root

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en' LC_ALL='en_US.UTF-8'
ENV TZ Asia/Seoul

ENV MODULE flow-server
ENV APP_HOME /xdp/apps/$MODULE

RUN mkdir -p $APP_HOME

WORKDIR $APP_HOME

COPY $MODULE/build/libs/$MODULE-all.jar .

USER xdp

ENTRYPOINT exec java $JAVA_OPTS -jar /xdp/apps/$MODULE/$MODULE-all.jar