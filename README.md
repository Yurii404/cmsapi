# Project Title

Course Management System API is an online management application. Its main purpose is to make efficient interaction
between students and instructors in college during the period of submission of assignments and for getting appropriate
feedback from instructors.


## Environment Variables

To run this project, you will need to add the following environment variables to your .env file

`MYSQL_USER`

`MYSQL_ROOT_PASSWORD`

`MYSQL_PASSWORD`

`SPRING_DATASOURCE_CMSAPI_USER`

`SPRING_DATASOURCE_CMSAPI_PASSWORD`

`SPRING_DATASOURCE_SECURITY_USER`

`SPRING_DATASOURCE_SECURITY_PASSWORD`

`AWS_BUCKET_NAME`

`AWS_BUCKET_FOLDER_NAME`

`AWS_ACCESS_KEY`

`AWS_SECRET_KEY`

`JWT_SECRET`

`JWT_EXPIRATION`


## Installation

Download gradle and java

```bash
  brew install gradle
  brew install openjdk@17 

```

## Run Locally

Clone the project

```bash
  git clone https://github.com/Yurii404/cmsapi.git
```

Go to the project directory

```bash
  cd cmsapi
```

Build

```bash
  gradle clean build
```

Start using docekr-compose

```bash
  docker-compose build

  docker-compose up
```


## Running Tests

To run tests, run the following command from root or certain service folder

```bash
  gradle test
```


## Demo

Since the app have api gateway all url should be this pattern:

```bash
  {api_gateway_url}/{service-name}/{endpoint}
```
Example:
```bash
  http://localhost:8765/auth-service/auth/register
```