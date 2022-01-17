import resources.Resources.codeDirs

plugins {
    id("java-library")
    id("kotlin")
    id("maven-publish")
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

dependencies {
    //implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    //implementation(kotlin(appdependencies.Builds.STDLIB, Versions.kotlin))
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

            //artifact(tasks["sourcesJar"])
            //artifact(tasks["javadocJar"])
        }
    }

    repositories {
        maven {
            name = "kodi"
            url = uri("${buildDir}/publishing-repository")
        }
    }
}