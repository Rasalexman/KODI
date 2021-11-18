package appdependencies

object Builds {
    const val MIN_VERSION = 19
    const val COMPILE_VERSION = 31
    const val TARGET_VERSION = 31
    const val BUILD_TOOLS = "31.0.0"
    const val APP_ID = "com.mincor.kodiexample"

    const val STDLIB = "stdlib-jdk8"

    object App {
        const val VERSION_CODE = 10012
        const val VERSION_NAME = "10012"
    }

    object Kodi {
        const val VERSION_NAME = "1.5.20"
    }

    object KodiAndroidX {
        const val VERSION_CODE = 105020
        const val VERSION_NAME = Kodi.VERSION_NAME
    }

    object KodiReflect {
        const val VERSION_CODE = 10138
        const val VERSION_NAME = "1.1.38"
    }
}

