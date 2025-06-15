# reference-aws-localstack-docker-app
This Java project contains multiple pieces needed for a base microservice application that implements aws-localstack.

## Quick Start
First, make sure Docker Desktop is running and updated to the latest version.

After Docker Desktop is started, start up the aws-localstack instance, using the `docker-compose:up` goal under the aws-localstack module. 
In Intellij, this task can be found on the Maven tab under: `aws-localstack -> Plugins -> docker-compose -> docker-compose:up`

Next, start the registry service using spring-boot:run under `registry -> Plugins -> spring-boot -> spring-boot:run`.

After that, start the gateway service using spring-boot:run under `gateway -> Plugins -> spring-boot -> spring-boot:run`.

Finally, start up each of the microservices using using spring-boot:run under `microservice-# -> Plugins -> spring-boot -> spring-boot:run`.
You can start up multiple instances of the same service by running spring-boot:run multiple times.

## Modules
### api
This module uses OpenAPI to generate all the files needed for a contract first approach to an API. To build the OpenAPI files, run the `compile` goal under `api -> Lifecycle`.

### aws-localstack
AWS Localstack implementation, using Docker. This current setup is for an S3 service, DynamoDB, Lambda, and SQS.

This module can be started up with the composeUp goal. This will cause the localstack process to run as a background process. In Intellij, this goal can be found in the Maven tab, under:
`aws-localstack -> Plugins -> docker-compose -> docker-compose:up`

This process can be stopped with the docker-compose:down goal.

### gateway
This Eureka Gateway keeps track of the Eureka Client instances, and correct ports, and routes / load balances paths to each client.
This project's dependencies (specfically spring-cloud-starter-netflix-eureka-client) are VERY sensitive, so please be careful when updating the pom.xml.

### lambda-example
An example lambda project that can be zipped and deployed to localstack. Run the `buildZip` goal under `lambda-example -> Plugins -> exec -> exec:exec`.

The generated zip file (under the `build/distributions`) will be deployed to localstack on startup of any project that uses the `repository` module. 

### microservice-1
This is a Eureka Client. Many instances can be spun up at once.

### microservice-2
This is a Eureka Client, distinct from microservice-1. This is provided as an easy quick-start reference.

### registry
A Eureka Discovery Service, all Eureka clients register with this service.

### repository
Contains S3 bucket, DynamoDB repository, SQS, and SNS. Connects to aws localstack via the application.properties file.

### test-smtp-server
An implementation of fake-smtp-server. The SMTP server runs on port 5081, and messages can be viewed at http://localhost:5080. Note that Localstack Community Edition does not support SES (Simple Email Service), but Localstack Pro does.

This module can be started up with the composeUp task. This will cause the localstack process to run as a background process. In Intellij, this task can be found in the Gradle tab, under:
`test-smtp-server -> Tasks -> docker -> composeUp`

This process can be stopped with the composeDown or composeDownForced tasks.

## Working with AWS Localstack

### Start up
You must start up the localstack instance by navigating to the directory containing the docker-compose file, and executing:

    docker-compose up

This will start up a localstack instance on your machine.

### Connecting
To set up a project to connect to localstack, have an application-local.properties file in your "resources" folder. This file should have the following line, indicating the localstack endpoint (in this case, running on port 4566).

    aws.end-point.url=http://localhost:4566

Your project's configuration should reference that property when configuring a connection to AWS. For example, in a java class tagged `@Configuration` you can have the following code to grab your aws endpoint:

    @Value("${aws.local.endpoint:#{null}}")
	private String awsEndpoint;

### Configuration
The configuration of AWS Localstack is contained in a single docker-compose.yml file. 

#### Services
To run an aws service on localstack, add the service name to:

        environment:
        - SERVICES=s3,dynamodb,lambda,sqs,sns

Available services can be found here:
https://docs.aws.amazon.com/cli/latest/reference/#available-services

A detailed list of localstack support levels (including Coverage Level, Emulation Level, and AWS Feature Coverage) for aws services can be found here:
https://docs.localstack.cloud/user-guide/aws/feature-coverage/

Coverage levels are:
```
⭐⭐⭐⭐⭐     Feature fully supported by LocalStack maintainers; feature is guaranteed to pass all or the majority of tests
⭐⭐⭐⭐       Feature partially supported by LocalStack maintainers
⭐⭐⭐  Feature supports basic functionalities (e.g., CRUD operations)
⭐⭐	Feature should be considered unstable
⭐	Feature is experimental and regressions should be expected
- Feature is not yet implemented
```

Emulation levels are:
```
CRUD: The service accepts requests and returns proper (potentially static) responses. No additional business logic besides storing entities.
Emulated: The service imitates the functionality, including synchronous and asynchronous business logic operating on service entities.
```

AWS Feature Coverage:
```
Community version (default, if not marked)
Pro version (marked with Pro)
```

## License

This project is licensed under the **Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)** license.  
You are free to:
- Share — copy and redistribute the material in any medium or format.
- Adapt — remix, transform, and build upon the material.

Under the following terms:
- **Attribution** — You must give appropriate credit, provide a link to the license, and indicate if changes were made.
- **NonCommercial** — You may not use the material for commercial purposes.

For more details, see the [license description](https://creativecommons.org/licenses/by-nc/4.0/).