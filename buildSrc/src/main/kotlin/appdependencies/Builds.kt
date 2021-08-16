package appdependencies

object Builds {
    const val MIN_VERSION = 19
    const val COMPILE_VERSION = 30
    const val TARGET_VERSION = 30
    const val BUILD_TOOLS = "30.0.2"
    const val APP_ID = "com.mincor.kodiexample"

    object App {
        const val VERSION_CODE = 10011
        const val VERSION_NAME = "10011"
    }

    object Kodi {
        const val VERSION_NAME = "1.5.16"
    }

    object KodiAndroidX {
        const val VERSION_CODE = 105016
        const val VERSION_NAME = Kodi.VERSION_NAME
    }

    object KodiReflect {
        const val VERSION_CODE = 10134
        const val VERSION_NAME = "1.1.34"
    }
}

