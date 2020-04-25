package resources

object Resources {

    object App {
        val dirs = arrayListOf(
                "src/main/res"
        )

        val javaDirs = arrayListOf(
                "app/build/generated/source/kaptKotlin/",
                "src/main/java"
        )
    }

    val resDirs = arrayListOf(
            "src/main/res"
    )
    val codeDirs = arrayListOf(
            "src/main/kotlin"
    )

    object KodiReflect {
        val dirs = arrayListOf(
                "src/main/res"
        )

        val javaDirs = arrayListOf(
                //"src/main/kotlin",
                "src/main/java"
        )
    }
}