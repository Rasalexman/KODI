import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    id("java-library")
    kotlin("jvm")
    id("maven-publish")
}

val codePath: String by rootProject.extra
val kodiVersion: String = libs.versions.kodiVersion.get()

val srcDirs = listOf(codePath)
group = "com.rasalexman.kodiksp"
version = kodiVersion

sourceSets {
    getByName("main") {
        java.setSrcDirs(srcDirs)
    }
}

//tasks.withType<KotlinJvmCompile>().configureEach {
//    compilerOptions {
//        apiVersion.set(KotlinVersion.KOTLIN_2_0)
//        languageVersion.set(KotlinVersion.KOTLIN_2_0)
//        jvmTarget.set(JvmTarget.JVM_21)
//        freeCompilerArgs.addAll(listOf(
//            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
//            "-opt-in=kotlin.RequiresOptIn"
//        ))
//    }
//}

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