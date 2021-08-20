//import org.jetbrains.dokka.gradle.DokkaTask
import appdependencies.Versions
import resources.Resources.codeDirs

plugins {
    id("java-library")
    id("kotlin")
    id("maven-publish")
    //id("org.jetbrains.dokka")
}

// Declare the task that will monitor all configurations.
configurations.all {
    // 2 Define the resolution strategy in case of conflicts.
    resolutionStrategy {
        // Fail eagerly on version conflict (includes transitive dependencies),
        // e.g., multiple different versions of the same dependency (group and name are equal).
        failOnVersionConflict()

        // Prefer modules that are part of this build (multi-project or composite build) over external modules.
        preferProjectModules()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withJavadocJar()
    withSourcesJar()
}

sourceSets {
    getByName("main") {
        java.setSrcDirs(codeDirs)
    }
}

dependencies {
    //implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    implementation(kotlin("stdlib", Versions.kotlin))
}

group = "com.rasalexman.kodi"
version = appdependencies.Builds.Kodi.VERSION_NAME


publishing {
    publications {
        create<MavenPublication>("kodi") {
            from(components["kotlin"])

            // You can then customize attributes of the publication as shown below.
            groupId = "com.rasalexman.kodi"
            artifactId = "kodi"
            version = appdependencies.Builds.Kodi.VERSION_NAME

            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
        }
    }

    repositories {
        maven {
            name = "kodi"
            url = uri("${buildDir}/publishing-repository")
        }
    }
}