# No Sweat - An Exercise Tracker

This project is a web app that allows creating exercises and workaround routines, as well as running exercises and
tracking your accomplishments.

This project includes both the frontend (web UI for user interactions) and the backend (which maintains the state of the
application for all users).

## Build steps

This project depends on [truthish](https://github.com/bitspittle/truthish) for multiplatform test logic, which isn't
hosted at the time of writing these docs, so first:

* `git clone https://github.com/bitspittle/truthish`
* `cd truthish`
* `./gradlew publishToLocalMaven`

Then you should be able to run this project by starting up the server:

* `cd nosweat`
* `./gradlew :backend:run`
  
At this point, you can start up the browser to test your changes:

* `./gradlew :frontend:jsBrowserRun`

## Technology

This project uses [Web Compose](https://compose-web.ui.pages.jetbrains.team/) for the frontend, pure Kotlin for the
backend, and [graphql](https://graphql.org/) to communicate between them.