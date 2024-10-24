plugins {
    id("java-library")
    id("kotlin")
    id("maven-publish")
    kotlin("kapt")
}

val codePath: String by rootProject.extra
val kodiVersion: String = libs.versions.kodiVersion.get()
val kotlinVersion: String = libs.versions.kotlin.get()

val srcDirs = listOf(codePath)

group = "com.rasalexman.kodispatcher"
version = kodiVersion

kapt {
    useBuildCache = true
    generateStubs = false
}

tasks.register<Jar>(name = "sourceJar") {
    from(sourceSets["main"].java.srcDirs)
    archiveClassifier.set("sources")
}

sourceSets {
    getByName("main") {
        java.setSrcDirs(srcDirs)
    }
}

java {
    this.sourceSets {
        getByName("main") {
            java.setSrcDirs(srcDirs)
        }
    }
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withJavadocJar()
    withSourcesJar()
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            this.useVersion(kotlinVersion)
        }
    }
}

dependencies {
    api(project(":kodikmp"))
}

publishing {
    publications {
        create<MavenPublication>("kodispatcher") {
            from(components["kotlin"])
            // You can then customize attributes of the publication as shown below.
            groupId = "com.rasalexman.kodispatcher"
            artifactId = "kodispatcher"
            version = kodiVersion

            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
        }
    }

    repositories {
        maven {
            name = "kodispatcher"
            url = uri("${layout.buildDirectory}/publishing-repository")
        }
    }
}