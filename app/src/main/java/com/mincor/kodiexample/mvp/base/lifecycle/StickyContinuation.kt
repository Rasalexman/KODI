package com.mincor.kodiexample.mvp.base.lifecycle

import kotlin.coroutines.Continuation

/**
 * This class designed for change configuration handling
 */
data class StickyContinuation<ReturnType> constructor(
        private val continuation: Continuation<ReturnType>,
        private var presenter: IBaseLifecyclePresenter<*>?,
        val strategy: StickyStrategy = StickyStrategy.Many
) : Continuation<ReturnType> by continuation {

    /**
     * Counter of how this sticky must be execute
     */
    private var _counter: Int = 0

    fun increaseCounter() {
        if(strategy is StickyStrategy.Counter) {
            _counter++
            val maxCountOfExecution = strategy.maxExecutionCounter
            if(maxCountOfExecution == _counter) {
                presenter?.removeStickyContinuation(this)
                clear()
            }
        }
    }

    fun clear() {
        presenter = null
    }
}