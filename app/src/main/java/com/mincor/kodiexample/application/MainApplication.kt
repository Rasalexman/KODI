package com.mincor.kodiexample.application

import android.app.Application
import android.content.Context
import com.kodi.generated.modules.localdatasource.localDataSourceModule
import com.kodi.generated.modules.presenters.presentersModule
import com.kodi.generated.modules.providers.providersModule
import com.kodi.generated.modules.remotedatasource.remoteDataSourceModule
import com.kodi.generated.modules.repository.repositoryModule
import com.kodi.generated.modules.usecasesdetails.useCasesDetailsModule
import com.kodi.generated.modules.usecasesgenres.useCasesGenresModule
import com.kodi.generated.modules.usecasesmovies.useCasesMoviesModule
import com.rasalexman.coroutinesmanager.AsyncTasksManager
import com.rasalexman.coroutinesmanager.CoroutinesManager
import com.rasalexman.coroutinesmanager.IAsyncTasksManager
import com.rasalexman.coroutinesmanager.ICoroutinesManager
import com.rasalexman.kodi.core.*

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        kodi {
            bind<Context>() with provider { applicationContext }
            bind<ICoroutinesManager>() with single { CoroutinesManager() }
            bind<IAsyncTasksManager>() with provider { AsyncTasksManager() }

            import(providersModule)

            import(useCasesDetailsModule)
            import(useCasesMoviesModule)
            import(useCasesGenresModule)

            import(remoteDataSourceModule)
            import(localDataSourceModule)
            import(repositoryModule)
            import(presentersModule)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        kodi {
            unbindAll()
        }
    }
}