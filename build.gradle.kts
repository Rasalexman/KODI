// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    apply(from="versions.gradle.kts")
    val kotlinVersion: String by extra
    val agpVersion: String by extra
    val navigation: String by extra
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
        classpath("com.android.tools.build:gradle:$agpVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navigation")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    apply(from="${rootDir}/versions.gradle.kts")
    val kotlinApiVersion: String by extra
    val jvmVersion: String by extra

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            this.apiVersion = kotlinApiVersion
            this.languageVersion = kotlinApiVersion
            this.jvmTarget = jvmVersion
            this.freeCompilerArgs += listOf(
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
