FROM public.ecr.aws/amazoncorretto/amazoncorretto:17

VOLUME /tmp
ENV DB_HOST=mysql
#ENV REDIS_HOST=redis
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]