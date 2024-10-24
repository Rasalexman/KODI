plugins {
    id("java-library")
    id("kotlin")
    id("maven-publish")
    id("org.jetbrains.kotlin.kapt")
}


val codePath: String by rootProject.extra
val kodiVersion: String = libs.versions.kodiVersion.get()

val srcDirs = listOf(codePath)
group = "com.rasalexman.kodigen"
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
    compileOnly(project(":kodikmp"))
    val kotlinpoet: String by rootProject.extra
    val autoService: String by rootProject.extra

    implementation(kotlinpoet)
    implementation(autoService)
    kapt(autoService)
}

publishing {
    publications {
        create<MavenPublication>("kodigen") {
            from(components["kotlin"])
            // You can then customize attributes of the publication as shown below.
            groupId = "com.rasalexman.kodigen"
            artifactId = "kodigen"
            version = kodiVersion

            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
        }
    }

    repositories {
        maven {
            name = "kodigen"
            url = uri("${layout.buildDirectory}/publishing-repository")
        }
    }
}


