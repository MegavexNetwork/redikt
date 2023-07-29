plugins {
    kotlin("jvm") version "1.9.0"
    `maven-publish`
    `jvm-test-suite`
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

@Suppress("UnstableApiUsage")
testing {
    suites {
        register<JvmTestSuite>("integrationTest") {
            dependencies {
                implementation(project())
            }
        }
    }
}

@Suppress("UnstableApiUsage")
tasks.named("check") {
    dependsOn(testing.suites.named("integrationTest"))
}

val integrationTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

tasks.test {
    useJUnitPlatform()
}

tasks.getByName<Test>("integrationTest") {
    project.findProperty("redis.uri")?.let { systemProperty("redis.uri", it) }
}

kotlin {
    explicitApi()
    jvmToolchain(17)
}

publishing.publications.create<MavenPublication>("maven") {
    from(components["java"])
}
