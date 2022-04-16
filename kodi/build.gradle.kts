plugins {
    id("java-library")
    kotlin("jvm")
    id("maven-publish")
}

val codePath: String by rootProject.extra
val kodiVersion: String by rootProject.extra

val srcDirs = listOf(codePath)
group = "com.rasalexman.kodi"
version = kodiVersion

sourceSets {
    getByName("main") {
        java.setSrcDirs(srcDirs)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        this.apiVersion = "1.6"
        this.languageVersion = "1.6"
        this.jvmTarget = "11"
    }
}

tasks.register<Jar>(name = "sourceJar") {
    from(sourceSets["main"].java.srcDirs)
    archiveClassifier.set("sources")
}

java {
    this.sourceSets {
        getByName("main") {
            java.setSrcDirs(srcDirs)
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
            version = kodiVersion

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