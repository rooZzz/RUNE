package org.jellyfin.androidtv.ui.presentation

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.leanback.widget.BaseGridView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.RowPresenter
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.ui.itemhandling.ItemRowAdapter

open class CustomListRowPresenter @JvmOverloads constructor(
    private val topPadding: Int? = null,
    private val startMargin: Int? = null
) : ListRowPresenter() {
    init {
        headerPresenter = CustomRowHeaderPresenter()
    }

    override fun isUsingDefaultShadow() = false

    @Suppress("UNUSED_PARAMETER")
    override fun onSelectLevelChanged(holder: RowPresenter.ViewHolder) {
        // No action needed
    }

    override fun onRowViewSelected(holder: RowPresenter.ViewHolder, selected: Boolean) {
        super.onRowViewSelected(holder, selected)
        if (!selected) return
        val listRowHolder = holder as? ListRowPresenter.ViewHolder ?: return
        val listRow = listRowHolder.row as? ListRow ?: return
        applyPendingSelection(listRowHolder, listRow)
    }

    // Main implementation that handles binding
    @Suppress("DEPRECATION")
    override fun onBindRowViewHolder(holder: RowPresenter.ViewHolder, item: Any) {
        super.onBindRowViewHolder(holder, item)
        updateView(holder, item)
    }

    override fun onUnbindRowViewHolder(holder: RowPresenter.ViewHolder) {
        val listRowHolder = holder as? ListRowPresenter.ViewHolder
        val listRow = listRowHolder?.row as? ListRow
        val rowAdapter = listRow?.adapter as? ItemRowAdapter
        rowAdapter?.setSelectionIndexListener(null)
        super.onUnbindRowViewHolder(holder)
    }

    private fun updateView(holder: RowPresenter.ViewHolder, item: Any) {
        val view = holder.view.parent as? View ?: return
        if (topPadding != null) {
            view.setPadding(view.paddingLeft, topPadding, view.paddingRight, view.paddingBottom)
        }
        if (startMargin != null) {
            val layoutParams = view.layoutParams as? ViewGroup.MarginLayoutParams
            layoutParams?.marginStart = startMargin
            view.layoutParams = layoutParams
        }

        // Hide header view when the item doesn't have one
        holder.headerViewHolder.view.isVisible = !(item is ListRow && item.headerItem == null)

        if (item is ListRow && holder is ListRowPresenter.ViewHolder) {
            val rowAdapter = item.adapter as? ItemRowAdapter
            if (isEpisodesRow(holder, item)) {
                configureCenteredAlignment(holder)
                rowAdapter?.setSelectionIndexListener { selectedIndex ->
                    applyPendingSelection(holder, item, selectedIndex)
                }
            } else {
                rowAdapter?.setSelectionIndexListener(null)
            }
            applyPendingSelection(holder, item)
        }
    }

    private fun applyPendingSelection(
        holder: ListRowPresenter.ViewHolder,
        row: ListRow,
        overrideIndex: Int? = null,
    ) {
        if (!isEpisodesRow(holder, row)) return
        val rowAdapter = row.adapter as? ItemRowAdapter
        val selectedIndex = overrideIndex ?: rowAdapter?.peekInitialSelectionIndex() ?: -1
        if (selectedIndex >= 0) {
            holder.gridView?.selectedPosition = selectedIndex
            rowAdapter?.markInitialSelectionApplied()
        }
    }

    private fun isEpisodesRow(holder: ListRowPresenter.ViewHolder, row: ListRow): Boolean {
        val episodesLabel = holder.view.context.getString(R.string.lbl_episodes)
        val headerName = row.headerItem?.name ?: return false
        return headerName == episodesLabel || headerName.endsWith(" $episodesLabel")
    }

    private fun configureCenteredAlignment(holder: ListRowPresenter.ViewHolder) {
        holder.gridView?.setWindowAlignment(BaseGridView.WINDOW_ALIGN_NO_EDGE)
        holder.gridView?.setWindowAlignmentOffsetPercent(50f)
        holder.gridView?.setItemAlignmentOffset(0)
        holder.gridView?.setItemAlignmentOffsetPercent(50f)
    }
}
