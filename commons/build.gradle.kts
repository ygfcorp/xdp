val jacksonVersion by extra { "2.12.1" }

dependencies {
    api("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    api("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    api("com.squareup.okhttp3:okhttp:4.9.0")
    api("com.squareup.okhttp3:logging-interceptor:4.9.0")
}
