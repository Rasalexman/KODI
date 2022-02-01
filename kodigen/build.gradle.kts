import resources.Resources.codeDirs

plugins {
    id("java-library")
    id("kotlin")
    id("maven-publish")
    kotlin("kapt")
}

group = "com.rasalexman.kodigen"
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

dependencies {
    //implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    //implementation(kotlin(appdependencies.Builds.STDLIB, appdependencies.Versions.kotlin))
    compileOnly(project(":kodi"))

    implementation(appdependencies.Libs.Processor.kotlinpoet)
    implementation(appdependencies.Libs.Processor.autoService)
    kapt(appdependencies.Libs.Processor.autoService)
}

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


