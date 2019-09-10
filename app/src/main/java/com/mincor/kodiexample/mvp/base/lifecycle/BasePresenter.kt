package com.mincor.kodiexample.mvp.base.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

typealias StickyBlock<V, R> = V.(StickyContinuation<R>) -> Unit

abstract class BasePresenter<V : IBaseView> : IBasePresenter<V> {
    override var view: V? = null

    private var viewLifecycle: Lifecycle? = null
    private val isViewResumed = AtomicBoolean(true)
    private val viewContinuations: MutableList<Continuation<V>> = mutableListOf()
    private val stickyContinuations: MutableMap<StickyContinuation<*>, StickyBlock<V, *>> =
        mutableMapOf()
    private var mustRestoreStickyContinuations: Boolean = false

    @Synchronized
    suspend fun view(): V {
        if (isViewResumed.get()) {
            view?.let { return it }
        }

        // We wait until the view is ready to be used again
        return suspendCoroutine { continuation -> viewContinuations.add(continuation) }
    }

    @Synchronized
    override fun attachView(view: V, viewLifecycle: Lifecycle) {
        this.view = view
        this.viewLifecycle = viewLifecycle
        viewLifecycle.addObserver(this)
        onViewAttached(view)
    }

    protected open fun onViewAttached(view: V) {
        // Nothing to do here. This is an event handled by the subclasses.
    }

    @Synchronized
    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    protected fun onViewStateChanged() {
        isViewResumed.set(viewLifecycle?.currentState?.isAtLeast(Lifecycle.State.RESUMED) ?: false)
    }

    @Synchronized
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected fun onViewReadyForContinuations() {
        val viewInstance = this.view
        if (viewInstance != null) {
            val viewContinuationsIterator = viewContinuations.listIterator()

            while (viewContinuationsIterator.hasNext()) {
                val continuation = viewContinuationsIterator.next()

                // The view was not ready when the presenter needed it earlier,
                // but now it's ready again so the presenter can continue
                // interacting with it.
                viewContinuationsIterator.remove()
                continuation.resume(viewInstance)
            }
        }
    }

    @Synchronized
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected fun onViewReadyForStickyContinuations() {
        val viewInstance = this.view
        if (viewInstance != null) {
            if (mustRestoreStickyContinuations) {
                mustRestoreStickyContinuations = false

                val stickyContinuationsIterator = stickyContinuations.iterator()

                while (stickyContinuationsIterator.hasNext()) {
                    val stickyContinuationBlockMap = stickyContinuationsIterator.next()
                    val stickyContinuation = stickyContinuationBlockMap.key
                    val stickyContinuationBlock = stickyContinuationBlockMap.value
                    viewInstance.stickyContinuationBlock(stickyContinuation)
                    stickyContinuation.checkStickyState()
                }
            }
        }
    }

    @Synchronized
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected open fun onViewDestroyed() {
        view = null
        this.viewLifecycle?.removeObserver(this)
        viewLifecycle = null
        mustRestoreStickyContinuations = true
    }

    fun onCleared() {
        cleanup()
    }

    @Synchronized
    private fun addStickyContinuation(
            continuation: StickyContinuation<*>,
            block: StickyBlock<V, *>
    ) {
        stickyContinuations[continuation] = block
    }

    @Synchronized
    override fun removeStickyContinuation(continuation: StickyContinuation<*>): Boolean {
        return stickyContinuations.remove(continuation)?.let {
            continuation.clear()
            true
        } ?: false
    }

    /**
     * Executes the given block on the view. The block is executed again
     * every time the view instance changes and the new view is resumed.
     * This, for example, is useful for dialogs that need to be persisted
     * across orientation changes.
     *
     * @param block code that has to be executed on the view
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun <ReturnType> V.stickySuspension(
            strategy: StickyStrategy = StickyStrategy.Many,
            block: StickyBlock<V, ReturnType>
    ): ReturnType {
        return suspendCoroutine { continuation ->
            val stickyContinuation: StickyContinuation<ReturnType> =
                StickyContinuation(continuation, this@BasePresenter, strategy)
            addStickyContinuation(stickyContinuation, block as V.(StickyContinuation<*>) -> Unit)
            block(stickyContinuation).also {
                stickyContinuation.checkStickyState()
            }
        }
    }

    @Synchronized
    open fun cleanup() {
        viewContinuations.clear()
        stickyContinuations.clear()
    }

    private fun <ReturnType> StickyContinuation<ReturnType>.checkStickyState() {
        when (this.strategy) {
            is StickyStrategy.Single -> removeStickyContinuation(this)
            is StickyStrategy.Counter -> this.increaseCounter()
            is StickyStrategy.Many -> Unit
        }
    }
}