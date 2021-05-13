// Add compose gradle plugin
plugins {
    kotlin("multiplatform")
}

group = "bitspittle"
version = "1.0-SNAPSHOT"

// Add maven repositories
repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js {
        browser()
    }
}
