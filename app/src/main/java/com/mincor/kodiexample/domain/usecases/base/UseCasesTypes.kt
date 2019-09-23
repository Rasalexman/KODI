package com.mincor.kodiexample.domain.usecases.base

interface IUseCase {
    interface In<in I> : IUseCase {
        suspend fun execute(data: I)
    }

    interface InOut<in I, out O> : IUseCase {
        suspend fun execute(data: I): O
    }

    interface Out<out O> : IUseCase {
        suspend fun execute(): O
    }
}
