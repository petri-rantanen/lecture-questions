FROM quay.io/quarkus/quarkus-distroless-image:2.0
WORKDIR /app
COPY target/*-runner /app/app
CMD ["/app/app"]
EXPOSE 8080
