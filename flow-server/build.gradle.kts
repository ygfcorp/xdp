import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    application
    id("org.jetbrains.kotlin.plugin.spring")
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.0.0"
}

val className = "com.kakao.xdp.flow.Main"

application {
    mainClass.set(className)
    mainClassName = className
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("flow-server")
        archiveVersion.set("")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to className))
        }
    }
}

dependencies {
    api(project(":commons"))
    api(project(":commons-server"))
    api(project(":commons-metric"))

    implementation("org.springframework.data:spring-data-jdbc:2.2.1")
    implementation("org.springframework:spring-tx:5.3.7")
    api("mysql:mysql-connector-java:8.0.25")
    implementation("org.apache.commons:commons-dbcp2:2.8.0")

    implementation("org.thymeleaf:thymeleaf-spring5:3.0.12.RELEASE")

    implementation("org.apache.nifi:nifi-client-dto:1.13.2")

    implementation("com.squareup.retrofit2:converter-gson:2.7.2")
    implementation("com.squareup.retrofit2:retrofit:2.7.2")

    implementation("com.googlecode.log4jdbc:log4jdbc:1.2")

    testImplementation("org.springframework:spring-test:5.3.7")
}
