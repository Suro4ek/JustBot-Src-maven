FROM alpine:3.13

RUN apk add openjdk8
COPY JustBot/TestDir/JustBot.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

