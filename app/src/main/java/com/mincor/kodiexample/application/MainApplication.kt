package com.mincor.kodiexample.application

import android.app.Application
import android.content.Context
import coil.Coil
import coil.ImageLoader
import coil.util.CoilLogger
import com.kodi.generated.module.presentersmodule.presentersModule
import com.mincor.kodiexample.BuildConfig
import com.mincor.kodiexample.modules.*
import com.rasalexman.coroutinesmanager.AsyncTasksManager
import com.rasalexman.coroutinesmanager.CoroutinesManager
import com.rasalexman.coroutinesmanager.IAsyncTasksManager
import com.rasalexman.coroutinesmanager.ICoroutinesManager
import com.rasalexman.kodi.core.*

class MainApplication : Application() {

    val kodi = kodi {
        bind<Context>() with provider { applicationContext }
        bind<ICoroutinesManager>() with single { CoroutinesManager() }
        bind<IAsyncTasksManager>() with provider { AsyncTasksManager() }


        //import(kodiAndroidXModule)
        import(databaseModule)
        import(networkModule)
        import(imageModule)
        import(localDataSourceModule)
        import(remoteDataSourceModule)
        import(repositoryModule)
        import(useCasesModule)
        import(presentersModule)
        //import(presentationModule)
    }

    override fun onCreate() {
        super.onCreate()

        if(BuildConfig.DEBUG) CoilLogger.setEnabled(false)
        Coil.setDefaultImageLoader(kodi { instance<ImageLoader>() })
    }

    override fun onTerminate() {
        super.onTerminate()
        kodi {
            unbindAll()
        }
    }
}