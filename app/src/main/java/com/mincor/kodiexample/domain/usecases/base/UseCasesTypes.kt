package com.mincor.kodiexample.domain.usecases.base

interface IUseCase {
    interface In<in I> : IUseCase {
        suspend operator fun invoke(data: I)
    }

    interface InOut<in I, out O> : IUseCase {
        suspend operator fun invoke(data: I): O
    }

    interface DoubleInOut<in I, in R, out O> : IUseCase {
        suspend operator fun invoke(first: I, second: R): O
    }

    interface Out<out O> : IUseCase {
        suspend operator fun invoke(): O
    }
}
