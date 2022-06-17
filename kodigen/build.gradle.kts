plugins {
    id("java-library")
    id("kotlin")
    id("maven-publish")
    kotlin("kapt")
}


val codePath: String by rootProject.extra
val kodiVersion: String by rootProject.extra
val apiKotlinVersion: String by extra
val jvmVersion: String by extra

val srcDirs = listOf(codePath)
group = "com.rasalexman.kodigen"
version = kodiVersion

sourceSets {
    getByName("main") {
        java.setSrcDirs(srcDirs)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        this.freeCompilerArgs += listOf(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
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
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withJavadocJar()
    withSourcesJar()
}

dependencies {
    compileOnly(project(":kodi"))
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
            url = uri("${buildDir}/publishing-repository")
        }
    }
}


