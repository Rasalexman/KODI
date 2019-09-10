package com.mincor.kodiexample.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mincor.kodiexample.R


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*val navController = findNavController(this, R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        // This line is only necessary if using the default action bar.
        setupActionBarWithNavController(navController, appBarConfiguration)*/
    }
}


fun log(tag: String = "----->", init:()->String) {
    println("$tag ${init()}")
}