package com.mincor.kodiexample

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.mincor.kodi.core.*
import com.mincor.kodiexample.single.UserData


class MainActivity : AppCompatActivity() {

    val kodi = kodi {
        bind<UserData>() with single { UserData("Alex", "Minkin", "sphc@yandex.ru") } at scope("")
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        val user = kodi { instance<UserData>() }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
