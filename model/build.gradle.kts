plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "bitspittle"
version = "1.0-SNAPSHOT"

// Add maven repositories
repositories {
    mavenLocal()
    mavenCentral()
}

// compile bytecode to java 8 (default is java 6)
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

kotlin {
    jvm()
    js(IR) {
        browser()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.json.core)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.json.jvm)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.json.js)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(libs.kotlin.reflect)
                implementation(libs.truthish.common)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation(libs.kotlintestrunner.junit5)
                implementation(libs.bundles.junit5)
                implementation(libs.truthish.jvm)
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
