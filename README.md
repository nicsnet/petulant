# caas

Crealytics Authentication and Authorisation Service.

A 'micro service' designed to authenticate users returning a generated JWS token for authorisation purposes.

## Prerequisites

You will need Leiningen https://github.com/technomancy/leiningen 2.0.0 or above installed.

## Setup

This project is still heavily under development, so setting things up is still a little quirky.

The database URL is set in an ENV variable: CAAS_DB_URL

Mine looks like this: CAAS_DB_URL=jdbc:postgresql://localhost:15432/caas?user=caas&password=cassonade
If your postgres instance is running on the default port, use something like: jdbc:postgresql://localhost:5432/caas?user=caas&password=cassonade

The database port may be different on your machine.


One you've created a database named: caas with the user: caas and password: cassonade things are good to go, and you can run migrations with ragtime

```bash
lein ragtime migrate
```

To migrate the test database do:

```bash
export CAAS_DB_URL_TEST='jdbc:postgresql://localhost:5432/caas_test?user=caas_test&password=cassonade_test'
lein with-profile test ragtime migrate
```

Then you can fire up a repl and create your first user.

```clojure
lein repl

(in-ns 'caas.models)

(def user {:email "foo@baz.de" :password "foobaz"})

(add-user! user)
```

This creates a user with the email "foo@baz.de" and stores the password in a hashed format using bcrypt+sha512 (others are available https://funcool.github.io/buddy-hashers/latest/).

## Usage

Start the server with

```bash
lein ring server
```

This starts a server on http://localhost:3000

```bash
curl -v -X GET http://localhost:3000/caas/authenticate\?email\=foo@baz.de\&password\=foobaz
```

Querying this route will return a signed JWS token.

```
eyJ0eXAiOiJKV1MiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImZvb0BiYXouZGUifQ.RA9A1xT_YJ-Xi5_2B9nASNgQ5FKGXOai1yy0nWqgq7k%
```

Once the token for this authenticated user is obtained, it can be used to query for permissions, e.g.

```
curl -v -X GET http://localhost:3000/caas/permissions\?token=
eyJ0eXAiOiJKV1MiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImZvb0BiYXouZGUifQ.RA9A1xT_YJ-Xi5_2B9nASNgQ5FKGXOai1yy0nWqgq7k%
```

This returns

```
{name:permission_name}

```

When a user cannot be authenticated or the JWT token string is not valid a HTTP code 401 "Unauthorized" is returned.

## TODOs

Some sort of basic HTTP authentication.
