Lease car microservice
===
- CUSTOMER microservice handle customer CURD operation and JWT authentication
- CAR microservice handle car CURD operation and calculate lease
- API-GATEWAY microservice for cloud gateway and filter/authenticate JWT
- EUREKA for server discovery and load-balance
- Packaging with "build-docker-image" profile will push image to dockerhub
- Customer and Car running on separate postgres database from docker-compose
- Rest-api documented via Swagger v2
- Actuator default /info endpoint active
- Zipkin for log tracing
- Full unit tested

### usage
Start up docker for db, zipkin
```text
docker compose up
```
Compile project on local or compile with "build-docker-image" profile push image then run that image from docker


### issues
Businesses logic need improve
