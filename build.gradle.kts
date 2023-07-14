import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
}

allprojects {
    group = "net.megavex"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        api("com.michael-bull.kotlin-result:kotlin-result:1.1.18")
        api("com.michael-bull.kotlin-result:kotlin-result-coroutines:1.1.18")

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")

        testImplementation(kotlin("test"))
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}
