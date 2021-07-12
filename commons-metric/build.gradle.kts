plugins {
    `java-library`
}

dependencies {
    api(project(":commons"))

    api("io.dropwizard.metrics:metrics-core:4.1.17")
    implementation("com.readytalk:metrics3-statsd:4.2.0")
}
