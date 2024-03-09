FROM openjdk:17-jdk-alpine
MAINTAINER alexafanasyev
COPY target/salary-calculator-1.0.1.jar salary-calc.jar
ENTRYPOINT ["java", "-jar", "/salary-calc.jar"]
EXPOSE 8585