# No Sweat - An Exercise Tracker

This project is a web app that allows creating exercises and workaround routines, as well as running exercises and
tracking your accomplishments.

This project includes both the frontend (web UI for user interactions) and the backend (which maintains the state of the
application for all users).

## Build steps

```shell
$ git clone https://github.com/bitspittle/nosweat
$ cd nosweat
```

### Submodule Dependencies

(You only need to do the steps in this section once)

Fetch submodule dependencies:

```shell
$ cd external
$ git submodule init
$ git submodule update
```

### Redis

The server uses Redis as a data store. You will need to download, install, and run a redis server before starting up the
backend.

See also: https://redis.io/topics/quickstart

Excerpt:
```shell
wget http://download.redis.io/redis-stable.tar.gz
tar xvzf redis-stable.tar.gz
cd redis-stable
make
```

then:

```shell
cd src/
./redis-server
```

### Run Server and Client

Then, navigate back to the root folder. At this point, you should be start up the server:

```shell
$ pwd
.../path/to/nosweat
$ ./gradlew :backend:run
```
  
Then, start up the browser to run the app:

```shell
$ ./gradlew :frontend:jsBrowserRun
```

## Technology

This project uses:

* [Web Compose](https://compose-web.ui.pages.jetbrains.team/) for the frontend
* [Ktor](https://ktor.io/) for the backend server
* [Redis](https://redis.io/) for the backend database
* [GraphQL](https://graphql.org/) for the protocol to communicate between frontend and backend

## Troubleshooting

---

Q: When I run the backend, it immediately exits with `JedisConnectionException: Could not get a resource from the pool`

A: You need to run `redis-server` first. See the [Redis](#Redis) section. 

---

Q: ?

A: ?