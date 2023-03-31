FROM gradle:8.0.2-jdk11
ADD ./ ./
RUN gradle shadow
EXPOSE 80/tcp
EXPOSE 80/udp
ENTRYPOINT ["java","-jar","./build/libs/Cn-NewBing_Proxy-1.0-SNAPSHOT-all.jar","80"]
