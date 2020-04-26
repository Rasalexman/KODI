package com.mincor.kodiexample.modules

import com.rasalexman.kodi.core.*
import com.mincor.kodiexample.domain.usecases.details.GetLocalDetailsUseCase
import com.mincor.kodiexample.domain.usecases.details.GetRemoteDetailsUseCase
import com.mincor.kodiexample.domain.usecases.details.IGetLocalDetailsUseCase
import com.mincor.kodiexample.domain.usecases.details.IGetRemoteDetailsUseCase
import com.mincor.kodiexample.domain.usecases.genres.*
import com.mincor.kodiexample.domain.usecases.movies.*

val useCasesModule = kodiModule {

    //---- Single Use-cases
    bind<IGetLocalDetailsUseCase>() with provider {
        GetLocalDetailsUseCase(
            instance()
        )
    }
    bind<IGetRemoteDetailsUseCase>() with provider {
        GetRemoteDetailsUseCase(
            instance()
        )
    }
    bind<IGetCachedMoviesUseCase>() with provider {
        GetCachedMoviesUseCase(
            instance()
        )
    }
    bind<IGetRemoteMoviesUseCase>() with provider {
        GetRemoteMoviesUseCase(
            instance()
        )
    }
    bind<IGetLocalGenresUseCase>() with provider {
        GetLocalGenresUseCase(
            instance()
        )
    }
    bind<IGetRemoteGenresUseCase>() with provider {
        GetRemoteGenresUseCase(
            instance()
        )
    }
    bind<IGetNewMoviesUseCase>() with provider {
        GetNewMoviesUseCase(
            instance()
        )
    }

    //---- Combined Use-cases
    bind<IGenresOutUseCase>() with provider {
        GetGenresUseCase(
            instance(),
            instance()
        )
    }
    bind<IGetMoviesInOutUseCase>() with provider {
        GetMoviesUseCase(
            instance(),
            instance()
        )
    }
    bind<IGetMovieDetailUseCase>() with provider {
        GetMovieDetailUseCase(
            instance(),
            instance()
        )
    }
    bind<IGetNextMoviesUseCase>() with provider {
        GetNextMoviesUseCase()
    }
}