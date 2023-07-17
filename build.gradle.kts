plugins {
    kotlin("jvm") version "1.9.0"
    `maven-publish`
}

group = "net.megavex"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api("io.ktor:ktor-network:2.3.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    explicitApi()
    jvmToolchain(17)
}

publishing.publications.create<MavenPublication>("maven") {
    from(components["java"])
}
