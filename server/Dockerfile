FROM adoptopenjdk/openjdk11:alpine-jre

ENV SBT_VERSION=1.3.13
ENV SBT_HOME=/usr/share/sbt
ENV PORT=8080

WORKDIR /usr/share
RUN wget -O sbt.tgz https://github.com/sbt/sbt/releases/download/v${SBT_VERSION}/sbt-${SBT_VERSION}.tgz 
RUN tar xvf sbt.tgz
RUN apk add bash

RUN ln -s "${SBT_HOME}/bin/"* "/usr/bin/"
RUN rm sbt.tgz

ADD . /code
WORKDIR /code
RUN sbt clean
RUN sbt stage
CMD [ "/code/http/target/universal/stage/bin/http" ]

RUN mv /code/aws-lambda-rie /usr/local/bin/aws-lambda-rie
RUN chmod +x /usr/local/bin/aws-lambda-rie
ADD entry-point.sh /entry-point.sh
RUN chmod +x /entry-point.sh

RUN rm -rf ${SBT_HOME}
WORKDIR /