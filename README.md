# caas

Crealytics Authentication and Authorisation Service.

A 'micro service' designed to authenticate users returning a generated JWS token for authorization purposes.

## Setup

This project is still heavily under development, so setting things up is still a little quirky.

The database URL is set in an ENV variable: CAAS_DB_URL

Mine looks like this: CAAS_DB_URL=jdbc:postgresql://localhost:15432/caas?user=caas&password=cassonade

The database port may be different on your machine.

TODO: The database config for the usage with korma http://sqlkorma.com/ is still hardcoded into src/caas/models.clj, so this needs to be replaced with an environment config.

One you've created a database named: cass with the user: caas and password: cassonade things are good to go, and you can run migrations with ragtime

```clojure
lein ragtime migrate
```

Then you can fire up a repl and create your first user.

```clojure
lein repl

(in-ns 'caas.models)

(def user {:email "foo@baz.de" password: "foobaz"})

(add-user! user)
```

This creates a user with the email "foo@baz.de" and stores the password in a hashed format using bcrypt+sha512 (others are available https://funcool.github.io/buddy-hashers/latest/).

## Usage

Start the server with 

```clojure
lein ring server
```

This starts a server on localhost:3000.

```
curl -v -X GET http://localhost:3000/authenticate\?email\=foo@baz.de\&password\=foobaz
```

Querying this route will return a signed JWS token.

```
eyJ0eXAiOiJKV1MiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImZvb0BiYXouZGUifQ.RA9A1xT_YJ-Xi5_2B9nASNgQ5FKGXOai1yy0nWqgq7k%
```







## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
