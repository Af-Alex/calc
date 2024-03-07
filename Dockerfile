FROM openjdk:17-jdk-alpine
MAINTAINER alexafanasyev
COPY target/salary-calculator-1.0.1.jar etc/salary-calc/salary-calc.jar
EXPOSE 8585
ENTRYPOINT ["java", "-jar", "/etc/salary-calc/salary-calc.jar"]