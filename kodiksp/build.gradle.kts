import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java-library")
    kotlin("jvm")
    id("maven-publish")
}

val codePath: String by rootProject.extra
val kodiVersion: String by rootProject.extra

val srcDirs = listOf(codePath)
group = "com.rasalexman.kodiksp"
version = kodiVersion

sourceSets {
    getByName("main") {
        java.setSrcDirs(srcDirs)
    }
}

val kotlinApiVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
        apiVersion.set(kotlinApiVersion)
        languageVersion.set(kotlinApiVersion)
        this.freeCompilerArgs = listOf(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlin.RequiresOptIn"
        )
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
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    withJavadocJar()
    withSourcesJar()
}


dependencies {
    api(project(":kodi"))

    implementation(libs.kotlinpoetKsp)
    implementation(libs.kspapi)
}

publishing {
    publications {
        create<MavenPublication>("kodiksp") {
            from(components["kotlin"])
            // You can then customize attributes of the publication as shown below.
            groupId = "com.rasalexman.kodiksp"
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