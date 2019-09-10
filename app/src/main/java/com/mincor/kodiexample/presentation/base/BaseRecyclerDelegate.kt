package com.mincor.kodiexample.presentation.base

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.ui.items.ProgressItem
import com.mincor.kodiexample.common.EndlessRecyclerViewScrollListener
import com.mincor.kodiexample.common.ScrollPosition
import com.mincor.kodiexample.common.toast
import com.mincor.kodiexample.common.unsafeLazy
import com.mincor.kodiexample.mvp.base.delegation.BaseDelegate
import com.mincor.kodiexample.mvp.base.lifecycle.IBasePresenter
import com.mincor.kodiexample.mvp.base.lifecycle.IBaseView

abstract class BaseRecyclerDelegate<V, P>(presenter: P) : BaseDelegate<V, P>(presenter)
    where V : IBaseView, P : IBasePresenter<V> {

    abstract val recyclerViewId: Int

    abstract val fragment: Fragment?

    private var isLoading: Boolean = false

    protected var recycler: RecyclerView? = null


    // current iterating item
    protected var currentItem: AbstractItem<*>? = null

    // layout manager for recycler
    protected open var recyclerLayoutManager: RecyclerView.LayoutManager? = null
    // направление размещения элементов в адаптере
    protected open val layoutManagerOrientation: Int = LinearLayoutManager.VERTICAL
    // бесконечный слушатель слушатель скролла
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    // custom decorator
    protected open var itemDecoration: RecyclerView.ItemDecoration? = null

    // main adapter items holder
    private val itemAdapter: ItemAdapter<AbstractItem<*>> by unsafeLazy { ItemAdapter<AbstractItem<*>>() }
    // save our FastAdapter
    protected val mFastItemAdapter: FastAdapter<*> by unsafeLazy { FastAdapter.with(itemAdapter) }

    // последняя сохраненная позиция (index & offset) прокрутки ленты
    protected val previousPosition: ScrollPosition = ScrollPosition()
    // крутилка прогресса)
    private val progressItem by unsafeLazy { ProgressItem().apply { isEnabled = false } }
    // корличесвто элементов до того как пойдет запрос на скролл пагинацию
    protected val visibleScrollCount get() = 5

    override fun delegate() {
        with(fragment) {
            recycler = this?.view?.findViewById(recyclerViewId)
        }

        setRVLayoutManager()     // менеджер лайаута
        setItemDecorator()       // декорации
        setRVCAdapter()          // назначение
        addClickListener()
        addEventHook()           // для нажатия внутри айтемов
    }

    override fun onViewDestroy() {
        savePreviousPosition()
        clearFastAdapter()
        clearRecycler()

        recyclerLayoutManager = null
        scrollListener = null
        itemDecoration = null
        recycler = null
        scrollListener = null
        currentItem = null
    }

    open fun showItems(list: List<AbstractItem<*>>) {
        hideLoading()
        if (list.isNotEmpty()) {
            FastAdapterDiffUtil[itemAdapter] = list
            scrollToTop()
        }
    }

    open fun addNewItems(list: List<AbstractItem<*>>) {
        hideLoading()
        if (list.isNotEmpty()) {
            FastAdapterDiffUtil[itemAdapter] = FastAdapterDiffUtil.calculateDiff(itemAdapter, list)
            scrollToTop()
        }
    }

    open fun addItems(list: List<AbstractItem<*>>) {
        hideLoading()
        if (list.isNotEmpty()) {
            itemAdapter.add(list)
        }
    }

    // менеджер лайаута
    protected open fun setRVLayoutManager() {
        recycler?.apply {
            recyclerLayoutManager = LinearLayoutManager(context, layoutManagerOrientation, false)
            layoutManager = recyclerLayoutManager
        }
    }

    // промежутки в адаптере
    protected open fun setItemDecorator() {
        if (recycler?.itemDecorationCount == 0) {
            itemDecoration?.let { recycler?.addItemDecoration(it) }
        }
    }

    protected open fun addClickListener() {
        mFastItemAdapter.onClickListener = { _, _, item, position ->
            if(item is AbstractItem<*>) onItemClickHandler(item, position)
            false
        }
    }

    //назначаем адаптеры
    protected open fun setRVCAdapter(isFixedSizes: Boolean = false) {
        recycler?.adapter ?: let {
            recycler?.setHasFixedSize(isFixedSizes)
            recycler?.swapAdapter(mFastItemAdapter, true)
        }
    }

    //
    protected open fun addEventHook() {}

    open fun showError(error: String?) {
        currentItem = null
        hideLoading()
        error?.let { errorMessage ->
            fragment?.toast(errorMessage)
        }
    }

    // слушатель бесконечная прокрутка
    protected fun setRVCScroll() {
        scrollListener = scrollListener ?: object : EndlessRecyclerViewScrollListener(recyclerLayoutManager, visibleScrollCount) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                onLoadNextPageHandler(page)
            }
        }
        recycler?.addOnScrollListener(scrollListener!!)
    }

    // Показываем загрузку
    open fun showLoading() {
        hideLoading()
        itemAdapter.add(progressItem)
        isLoading = true
    }

    // прячем загрузку
    open fun hideLoading() {
        val position = itemAdapter.getAdapterPosition(progressItem)
        if (position > -1) itemAdapter.remove(position)
        isLoading = false
    }

    //--------- CALL BACKS FOR RECYCLER VIEW ACTIONS
    protected open fun onLoadNextPageHandler(page: Int) {}

    protected open fun onItemClickHandler(item: AbstractItem<*>, position: Int) = Unit

    // если хотим сохранить последнюю проскролленную позицию
    protected open fun savePreviousPosition() {
        recycler?.let { rec ->
            previousPosition.let { scrollPosition ->
                val v = rec.getChildAt(0)
                scrollPosition.index = (rec.layoutManager as? LinearLayoutManager?)?.findFirstVisibleItemPosition() ?: 0
                scrollPosition.top = v?.let { it.top - rec.paddingTop } ?: 0
            }
        }
    }

    // прокручиваем к ранее выбранным элементам
    open fun applyScrollPosition() {
        recycler?.let { rec ->
            stopRecyclerScroll()
            previousPosition.let {
                (rec.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(it.index, it.top)
            }
        }
    }

    // скролл к верхней записи
    protected fun scrollToTop() {
        stopRecyclerScroll()
        // сбрасываем прокрутку
        previousPosition.drop()
        // применяем позицию
        applyScrollPosition()
    }

    private fun stopRecyclerScroll() {
        // останавливаем прокрутку
        recycler?.stopScroll()
    }

    private fun clearAdapter() {
        scrollListener?.resetState()
        itemAdapter.clear()
        mFastItemAdapter.notifyAdapterDataSetChanged()
        mFastItemAdapter.notifyDataSetChanged()
    }

    private fun clearFastAdapter() {
        clearAdapter()
        mFastItemAdapter.apply {
            onClickListener = null
            eventHooks.clear()
        }
    }

    private fun clearRecycler() {
        recycler?.apply {
            removeAllViews()
            removeAllViewsInLayout()
            adapter = null
            layoutManager = null
            itemAnimator = null
            clearOnScrollListeners()
            recycledViewPool.clear()

            itemDecoration?.let {
                removeItemDecoration(it)
            }
            itemDecoration = null
        }
    }

    protected fun removeCurrentItemFromAdapter() {
        currentItem?.let {
            itemAdapter.getAdapterPosition(it).let { position ->
                itemAdapter.remove(position)
                currentItem = null
            }
        }
    }
}