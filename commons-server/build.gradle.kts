
val springVersion by extra { "5.3.7" }
val undertowVersion by extra { "2.2.3.Final" }
val xnioVersion by extra { "3.8.4.Final" }

dependencies {
    api(project(":commons"))
    api(project(":commons-metric"))

    api("javax.annotation:javax.annotation-api:1.3.2")

    api("org.springframework:spring-context:$springVersion")
    api("org.springframework:spring-webmvc:$springVersion")
    api("org.springframework:spring-aop:$springVersion")

    api("org.aspectj:aspectjweaver:1.9.6")

    api("io.undertow:undertow-servlet:$undertowVersion")
    implementation("org.jboss.xnio:xnio-nio:$xnioVersion")

    api("com.github.ben-manes.caffeine:caffeine:3.0.0")
}
