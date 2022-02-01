import resources.Resources.codeDirs

plugins {
    id("java-library")
    id("kotlin")
    id("maven-publish")
}

group = "com.rasalexman.kodi"
version = appdependencies.Builds.Kodi.VERSION_NAME

sourceSets {
    getByName("main") {
        java.setSrcDirs(codeDirs)
    }
}

tasks.create(name = "sourceJar", type = Jar::class) {
    from(sourceSets["main"].java.srcDirs)
    archiveClassifier.set("sources")
}

java {
    this.sourceSets {
        getByName("main") {
            java.setSrcDirs(codeDirs)
        }
    }
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withJavadocJar()
    withSourcesJar()
}

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