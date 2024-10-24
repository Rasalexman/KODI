plugins {
    id("java-library")
    kotlin("jvm")
    id("maven-publish")
}

val kodiKmpNamespace: String by extra
val codePath: String by rootProject.extra
val kodiVersion: String = libs.versions.kodiVersion.get()

val srcDirs = listOf(codePath)
group = kodiKmpNamespace
version = kodiVersion

sourceSets {
    getByName("main") {
        java.setSrcDirs(srcDirs)
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
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withJavadocJar()
    withSourcesJar()
}


dependencies {
    api(project(":kodikmp"))
    val kotlinpoetKsp: String by rootProject.extra
    val kspapi: String by rootProject.extra

    implementation(kotlinpoetKsp)
    implementation(kspapi)
}

publishing {
    publications {
        create<MavenPublication>("kodiksp") {
            from(components["kotlin"])
            // You can then customize attributes of the publication as shown below.
            groupId = kodiKmpNamespace
            artifactId = "kodiksp"
            version = kodiVersion

            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
        }
    }

    repositories {
        maven {
            name = "kodiksp"
            url = uri("${layout.buildDirectory}/publishing-repository")
        }
    }
}