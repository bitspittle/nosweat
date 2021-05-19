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

This project uses [Web Compose](https://compose-web.ui.pages.jetbrains.team/) for the frontend, [Ktor](https://ktor.io/)
for the backend server, and the [graphql](https://graphql.org/) protocol to communicate between them.