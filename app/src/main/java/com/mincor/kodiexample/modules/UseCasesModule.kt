package com.mincor.kodiexample.modules

import com.mincor.kodi.core.bind
import com.mincor.kodi.core.instance
import com.mincor.kodi.core.kodiModule
import com.mincor.kodi.core.provider
import com.mincor.kodiexample.domain.usecases.details.GetLocalDetailsUseCase
import com.mincor.kodiexample.domain.usecases.details.GetRemoteDetailsUseCase
import com.mincor.kodiexample.domain.usecases.genres.GetGenresUseCase
import com.mincor.kodiexample.domain.usecases.genres.GetLocalGenresUseCase
import com.mincor.kodiexample.domain.usecases.genres.GetRemoteGenresUseCase
import com.mincor.kodiexample.domain.usecases.movies.*

val useCasesModule = kodiModule {

    //---- Single Use-cases
    bind<GetLocalDetailsUseCase>() with provider {
        GetLocalDetailsUseCase(
            instance()
        )
    }
    bind<GetRemoteDetailsUseCase>() with provider {
        GetRemoteDetailsUseCase(
            instance()
        )
    }
    bind<GetCachedMoviesUseCase>() with provider {
        GetCachedMoviesUseCase(
            instance()
        )
    }
    bind<GetRemoteMoviesUseCase>() with provider {
        GetRemoteMoviesUseCase(
            instance()
        )
    }
    bind<GetLocalGenresUseCase>() with provider {
        GetLocalGenresUseCase(
            instance()
        )
    }
    bind<GetRemoteGenresUseCase>() with provider {
        GetRemoteGenresUseCase(
            instance()
        )
    }
    bind<GetNewMoviesUseCase>() with provider {
        GetNewMoviesUseCase(
            instance()
        )
    }

    //---- Combined Use-cases
    bind<GetGenresUseCase>() with provider {
        GetGenresUseCase(
            instance(),
            instance()
        )
    }
    bind<GetMoviesUseCase>() with provider {
        GetMoviesUseCase(
            instance(),
            instance()
        )
    }
    bind<GetMovieDetailUseCase>() with provider {
        GetMovieDetailUseCase(
            instance(),
            instance()
        )
    }
    bind<GetNextMoviesUseCase>() with provider {
        GetNextMoviesUseCase()
    }
}