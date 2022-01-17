import appdependencies.Versions
import resources.Resources.codeDirs

plugins {
    id("java-library")
    id("kotlin")
    id("maven-publish")
    kotlin("kapt")
}

kapt {
    useBuildCache = true
    generateStubs = false
}

sourceSets {
    getByName("main") {
        java.setSrcDirs(codeDirs)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withJavadocJar()
    withSourcesJar()
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            this.useVersion(Versions.kotlin)
        }
    }
}

dependencies {
    //implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    //implementation(kotlin("stdlib", Versions.kotlin))
    api(project(":kodi"))
}

group = "com.rasalexman.kodispatcher"
version = appdependencies.Builds.Kodi.VERSION_NAME

publishing {
    publications {
        create<MavenPublication>("kodispatcher") {
            from(components["kotlin"])
            // You can then customize attributes of the publication as shown below.
            groupId = "com.rasalexman.kodispatcher"
            artifactId = "kodispatcher"
            version = appdependencies.Builds.Kodi.VERSION_NAME

            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
        }
    }

    repositories {
        maven {
            name = "kodispatcher"
            url = uri("${buildDir}/publishing-repository")
        }
    }
}