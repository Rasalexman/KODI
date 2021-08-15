package com.mincor.kodiexample.activity

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import coil.Coil
import coil.ImageLoader
import com.mincor.kodiexample.R
import com.rasalexman.kodi.core.instance
import com.rasalexman.kodi.core.kodi


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Coil.setImageLoader(kodi { instance<ImageLoader>() })
    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navController = findNavController(this, R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        // This line is only necessary if using the default action bar.
        setupActionBarWithNavController(navController, appBarConfiguration)
    }*/
}