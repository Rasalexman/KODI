package com.mincor.kodiexample.activity

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.mincor.kodiexample.R


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.main_activity_layout)
        Navigation.findNavController(this, R.id.nav_host_fragment)
    }
}


fun log(tag: String = "----->", init:()->String) {
    println("$tag ${init()}")
}