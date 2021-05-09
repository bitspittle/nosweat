// Add compose gradle plugin
plugins {
    kotlin("multiplatform") version "1.4.32"
    id("org.jetbrains.compose") version "0.0.0-web-dev-11"
}

group = "bitspittle"
version = "1.0-SNAPSHOT"

// Add maven repositories
repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

// Enable JS(IR) target and add dependencies
kotlin {
    js(IR) {
        moduleName = "nosweat"
        browser {
            commonWebpackConfig {
                outputFileName = "nosweat.js"
            }
        }
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(compose.web.web)
                implementation(compose.runtime)
            }
        }
    }
}
