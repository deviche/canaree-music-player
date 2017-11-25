package dev.olog.presentation.fragment_detail

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import dev.olog.presentation.BR
import dev.olog.presentation.R
import dev.olog.presentation._base.DataBoundViewHolder
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.ApplicationContext
import dev.olog.shared.MediaIdHelper
import javax.inject.Inject

class DetailAdapter @Inject constructor(
        @ApplicationContext context: Context,
        @FragmentLifecycle lifecycle: Lifecycle,
        mediaId: String,
        private val recentSongsAdapter: DetailRecentlyAddedAdapter
) : RecyclerView.Adapter<DataBoundViewHolder<*>>() {

    private val source = MediaIdHelper.mapCategoryToSource(mediaId)

    private val dataController = DetailDataController(context, this, MediaIdHelper.mapCategoryToSource(mediaId))
    private val innerAdapter = DetailHorizontalAdapter(dataController.fakeData)
    private val recycled = RecyclerView.RecycledViewPool()

    init {
        lifecycle.addObserver(dataController)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<*> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, viewType, parent, false)
        val viewHolder = DataBoundViewHolder(binding)
        initViewHolderListeners(viewHolder, viewType)
        return viewHolder
    }

    private fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int){
        if (viewType == R.layout.item_most_played_horizontal_list) {
            val list = viewHolder.itemView as RecyclerView
            list.layoutManager = GridLayoutManager(viewHolder.itemView.context,
                    5, GridLayoutManager.HORIZONTAL, false)
            list.adapter = innerAdapter
            list.recycledViewPool = recycled

            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(list)
        } else if (viewType == R.layout.item_recent_horizontal_list){
            val list = viewHolder.itemView as RecyclerView
            list.layoutManager = GridLayoutManager(viewHolder.itemView.context,
                    5, GridLayoutManager.HORIZONTAL, false)
            list.adapter = recentSongsAdapter
            list.recycledViewPool = recycled

            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(list)
        }
    }

    override fun getItemCount(): Int = dataController.getSize()

    override fun onBindViewHolder(holder: DataBoundViewHolder<*>, position: Int) {
        bind(holder.binding, dataController[position], position)
        holder.binding.executePendingBindings()
    }

    private fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int){
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.source,  source)
        binding.setVariable(BR.position, position)
    }

    override fun getItemViewType(position: Int): Int = dataController[position].type

    fun getItem(position: Int): DisplayableItem = dataController[position]

    fun onItemChanged(item: DisplayableItem){
        dataController.publisher.onNext(DetailDataController.DataType.HEADER.to(listOf(item)))
    }

    fun onSongListChanged(list: List<DisplayableItem>){
        dataController.publisher.onNext(DetailDataController.DataType.SONGS.to(list))
    }

    fun onAlbumListChanged(list: List<DisplayableItem>){
        dataController.publisher.onNext(DetailDataController.DataType.ALBUMS.to(list))
    }

}