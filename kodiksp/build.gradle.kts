plugins {
    //id("java-library")
    //kotlin("jvm")
    kotlin("multiplatform")
    id("maven-publish")
}

val codePath: String by rootProject.extra
val kodiVersion: String by rootProject.extra
val apiKotlinVersion: String by extra
val jvmVersion: String by extra
val groupNameKsp: String by extra

val srcDirs = listOf(codePath)
group = groupNameKsp
version = kodiVersion

kotlin {
    jvm()

    val kotlinpoetKsp: String by rootProject.extra
    val kspapi: String by rootProject.extra

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":kodi"))
                implementation(kotlinpoetKsp)
                implementation(kspapi)
            }
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
        }
    }
}

//sourceSets {
//    getByName("main") {
//        java.setSrcDirs(srcDirs)
//    }
//}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        this.apiVersion = apiKotlinVersion
        this.languageVersion = apiKotlinVersion
        this.jvmTarget = jvmVersion
        this.freeCompilerArgs += listOf(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlin.RequiresOptIn",
            "-Xjdk-release=$jvmVersion"
        )
    }
}

//tasks.register<Jar>(name = "sourceJar") {
//    from(sourceSets["main"].java.srcDirs)
//    archiveClassifier.set("sources")
//}

//java {
//    this.sourceSets {
//        getByName("main") {
//            java.setSrcDirs(srcDirs)
//        }
//    }
//    sourceCompatibility = JavaVersion.VERSION_11
//    targetCompatibility = JavaVersion.VERSION_11
//
//    withJavadocJar()
//    withSourcesJar()
//}



//dependencies {
//    api(project(":kodi"))
//    val kotlinpoetKsp: String by rootProject.extra
//    val kspapi: String by rootProject.extra
//
//    implementation(kotlinpoetKsp)
//    implementation(kspapi)
//}

publishing {
    publications {
        create<MavenPublication>("kodiksp") {
            from(components["kotlin"])
            // You can then customize attributes of the publication as shown below.
            groupId = groupNameKsp
            artifactId = "kodiksp"
            version = kodiVersion

            //artifact(tasks["sourcesJar"])
            //artifact(tasks["javadocJar"])
        }
    }

    repositories {
        maven {
            name = "kodiksp"
            url = uri("${buildDir}/publishing-repository")
        }
    }
}