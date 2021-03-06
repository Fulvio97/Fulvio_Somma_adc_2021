FROM alpine/git
WORKDIR /app
RUN git clone https://github.com/Fulvio97/Fulvio_Somma_adc_2021.git

FROM maven:3.5-jdk-8-alpine
WORKDIR /app
COPY --from=0 /app/Fulvio_Somma_adc_2021 /app
RUN mvn package

FROM openjdk:8-jre-alpine
WORKDIR /app
ENV MASTER=127.0.0.1
ENV ID=1
COPY --from=1 /app/target/AnonymousChat.jar /app

CMD /usr/bin/java -jar AnonymousChat.jar $MASTER $ID
