# Introduction
This is a simple validation service for trade data. 
Validation is based on sets of validators defined per trade type.

# How to run
- `./gradlew bootRun` from the root of the project to run
- `client/client.sh client/payload-example.json` from the root of the project to test functionality on UNIX-based systems

# Features
- Spring Boot 1.5.10 stack
- JSON schema validation draft 6
- Swagger 2 with Swagger UI
- Drowizard metrics with console reporter