package com.munch.lib.extend.recyclerview

import android.view.View
import android.view.ViewGroup
import com.munch.lib.log


class SimpleExpandableAdapter<T : ExpandableLevelData>(
    resIds: MutableList<Int>,
    list: MutableList<T>? = null,
    private val onBind: (adapter: SimpleExpandableAdapter<T>, holder: BaseViewHolder, data: T, position: Int) -> Unit
) : ExpandableAdapter<T, BaseViewHolder>(resIds, list) {
    override fun onBind(holder: BaseViewHolder, data: T, position: Int) {
        onBind.invoke(this, holder, data, position)
    }
}

/**
 * 将展开的view当作RecyclerView不同type的子view，通过增减数据的形式实现展开、收回动画
 *
 * 如果展开view里符合GridView，可以直接将rv的layoutManager设置为GridLayoutManager来实现
 *
 * Create by munch182 on 2021/1/22 16:49.
 */
abstract class ExpandableAdapter<T : ExpandableLevelData, B : BaseViewHolder> private constructor(
    private val resIds: MutableList<Int>? = null,
    private val views: MutableList<View>? = null,
    list: MutableList<T>? = null
) : BaseAdapter<T, B>(0, list) {

    constructor(resIds: MutableList<Int>, list: MutableList<T>? = null) : this(
        resIds, arrayListOf(), list
    )

    constructor(views: MutableList<View>) : this(
        arrayListOf(), views, null
    )

    @Suppress("UNCHECKED_CAST")
    fun expand(data: ExpandableLevelData) {
        val position = getData().indexOf(data)
        val expandData = getData(position).getExpandableData() as? MutableList<T>? ?: return
        add(position + 1, expandData)
    }

    fun reduce(data: ExpandableLevelData) {
        val pos = getData().indexOf(data)
        val size = getData(pos).getExpandableData()?.size ?: return
        remove(pos + 1, pos + 1 + size)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): B {
        if (resIds != null) {
            return BaseViewHolder(resIds[viewType], parent) as B
        } else if (views != null) {
            return BaseViewHolder(views[viewType]) as B
        }
        throw throw UnsupportedOperationException("need itemView")
    }

    override fun getItemViewType(position: Int): Int {
        return getData(position).expandLevel()
    }

}

interface ExpandableLevelData {

    /**
     * 展开的层级，最开始的为0，逐层加1
     */
    fun expandLevel(): Int = 0

    fun getExpandableData(): MutableList<ExpandableLevelData>? = null
}