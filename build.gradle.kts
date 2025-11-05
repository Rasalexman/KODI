import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    apply(from="versions.gradle.kts")
    val jitpackPath: String by extra
    val pluginsPath: String by extra

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri(jitpackPath) }
        maven { url = uri(pluginsPath) }
    }
    dependencies {
        classpath(libs.agp)
        classpath(libs.kgp)
        classpath(libs.safeargs)

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    apply(from="${rootDir}/versions.gradle.kts")
    val kotlinApiVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2
    val jvmVersion: String by extra

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
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

    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = jvmVersion
        targetCompatibility = jvmVersion
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
