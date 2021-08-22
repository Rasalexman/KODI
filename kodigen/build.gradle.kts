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
    includeCompileClasspath = false
}

sourceSets {
    getByName("main") {
        java.setSrcDirs(codeDirs)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withJavadocJar()
    withSourcesJar()
}

/*configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            this.useVersion(appdependencies.Versions.kotlin)
        }
    }
}*/

dependencies {
    //implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    implementation(kotlin(appdependencies.Builds.STDLIB, appdependencies.Versions.kotlin))
    implementation(project(":kodi"))

    implementation("com.squareup:kotlinpoet:1.8.0")
    implementation("com.google.auto.service:auto-service:1.0")
    kapt("com.google.auto.service:auto-service:1.0")
}

group = "com.rasalexman.kodigen"
version = appdependencies.Builds.Kodi.VERSION_NAME

publishing {
    publications {
        create<MavenPublication>("kodigen") {
            from(components["kotlin"])
            // You can then customize attributes of the publication as shown below.
            groupId = "com.rasalexman.kodigen"
            artifactId = "kodigen"
            version = appdependencies.Builds.Kodi.VERSION_NAME

            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
        }
    }

    repositories {
        maven {
            name = "kodigen"
            url = uri("${buildDir}/publishing-repository")
        }
    }
}


