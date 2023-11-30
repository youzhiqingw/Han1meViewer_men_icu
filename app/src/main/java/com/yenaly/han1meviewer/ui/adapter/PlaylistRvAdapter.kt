package com.yenaly.han1meviewer.ui.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.yenaly.han1meviewer.R
import com.yenaly.han1meviewer.logic.model.PlaylistsModel
import com.yenaly.han1meviewer.ui.fragment.home.MyPlaylistFragment
import com.yenaly.han1meviewer.util.notNull

/**
 * @project Han1meViewer
 * @author Yenaly Liew
 * @time 2023/11/26 026 16:30
 */
class PlaylistRvAdapter(private val fragment: Fragment) :
    BaseQuickAdapter<PlaylistsModel.Playlist, QuickViewHolder>() {

    init {
        isStateViewEnable = true
    }

    override fun onBindViewHolder(
        holder: QuickViewHolder,
        position: Int,
        item: PlaylistsModel.Playlist?,
    ) {
        item.notNull()
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_count, item.total.toString())
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_playlist, parent).also { viewHolder ->
            check(fragment is MyPlaylistFragment)
            viewHolder.itemView.setOnClickListener {
                val position = viewHolder.bindingAdapterPosition
                val item = getItem(position).notNull()
                fragment.listCode = item.listCode
                fragment.listTitle = item.title
                fragment.getNewPlaylistItems()
                fragment.binding.dlPlaylist.closeDrawers()
            }
        }
    }
}