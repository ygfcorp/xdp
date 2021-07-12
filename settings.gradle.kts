rootProject.name = "xdp"

include(":commons")
include(":commons-metric")
include(":commons-server")

include(":flow-server")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven ("https://dl.bintray.com/gradle/gradle-plugins")
    }
}
