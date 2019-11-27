# Coding Challenge

Design and implement a RESTful API (including data model and the backing implementation)
for money transfers between accounts.

## Explicit requirements:
1. You can use Java, Scala or Kotlin.
2. Keep it simple and to the point (e.g. no need to implement any authentication).
3. Assume the API is invoked by multiple systems and services on behalf of end users.
4. You can use frameworks/libraries if you like (except Spring), but don't forget about
requirement #2 – keep it simple and avoid heavy frameworks.
5. The datastore should run in-memory for the sake of this test.
6. The final result should be executable as a standalone program (should not require
a pre-installed container/server).
7. Demonstrate with tests that the API works as expected.

## Implicit requirements:
1. The code produced by you is expected to be of high quality.
2. There are no detailed requirements, use common sense.

**Please put your work on github or bitbucket.**


# Solution - Transfer Service

## How to run:
To build this project, make sure that mvn command is available in the path and your current working directory is the root of the project, execute the following from a project root directory.

```
mvn clean package
java -jar target/transfer-service-1.0-SNAPSHOT.jar
```

By default, service should be available at `http://localhost:8080/` 
A primitive RESTful implementation of a money transfering service.

## Solution details:

1. Java is used for the implementation.
2. Used libraries:
    - Maven - maven shade plugin for fat jar
    - SparkJava - a micro web framework
    - Guice - a lightweight dependency injection
    - Jdbi - a lightweight framework for DB abstraction
    - sl4j - for logging
    - Gson - Json serde library
    - Junit - a well-known and simple framework for unit tests
    - Unirest - a thin HTTP library used for testing purpose
    - Mockito - a simple mocking framework for unit tests
    - Assertj - assertions framework

## Api details:

**Available methods**

| Method                                              | Description            |
|-----------------------------------------------------|------------------------|
| GET /v{versionId}/accounts/{accountId}              | Get account by its ID  |
| POST /v{versionId}/accounts                         | Create a new account   |
| GET /v{versionId}/accounts/{accountId}/transfers    | Account transfers      |
| POST /v{versionId}/transfers                        | Make transfer          |
| GET /v{versionId}/transfers                         | Show all transfers     |

## Models:

>GET /accounts/{accountId}

**Request**

N/A

**Response Successfully**

HTTP_CODE : 200

```
{
    "owner" : "jhon",
    "amount" : 230
}
```

>POST /accounts

**Request**

```
{
    "owner" : "jhon",
    "amount" : 230
}
```

**Response Successfully**


HTTP_CODE if is successfully: 201

```
{
    "id": 1232
}
```

>GET /accounts/{accountId}/transfers

**Request**

N/A

**Response Successfully**

HTTP_CODE : 200

```
{
    "data": [
        {
            "fromAccount": 1003,
            "toAccount": 1004,
            "amount": 23.33,
            "transferDate": "2019-11-11 12:12:12"
        },
        {
            "fromAccount": 1004,
            "toAccount": 1003,
            "amount": 13.00,
            "transferDate": "2019-11-11 12:12:12"
        }
    ]
}
```


> POST /transfers

**Request**

```
{
    "from-account":  "123",
    "to-account":  "22",
    "amount": 23
}
```

**Response Successfully**

HTTP_CODE: 204

```
```

>GET /transfers

**Request**

N/A

**Response Successfully**

HTTP_CODE : 200

```
{
    "data": [
        {
            "fromAccount": 1003,
            "toAccount": 1004,
            "amount": 23.33,
            "transferDate": "2019-11-11 12:12:12"
        },
        {
            "fromAccount": 1004,
            "toAccount": 1003,
            "amount": 13.00,
            "transferDate": "2019-11-11 12:12:12"
        }
    ]
}
```
A postman collection is included.