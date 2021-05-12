import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
}

group = "bitspittle"
version = "1.0-SNAPSHOT"

// Add maven repositories
repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.kotlin.jvm)
    implementation(libs.bundles.kgraphql)
    implementation(libs.ktor.core)
    implementation(libs.logback)

    testImplementation(libs.ktor.server.tests)
}

// compile bytecode to java 8 (default is java 6)
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("bitspittle.nosweat.server.ApplicationKt")
}