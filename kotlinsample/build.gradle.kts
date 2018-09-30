import java.net.URI

plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "pl.mareklangiewicz.myktor.kotlinsample.MainKt"
}

dependencies {
    implementation(Deps.kotlinStdlib8)
    implementation(project(":myktor"))
    testImplementation(Deps.junit)
}

