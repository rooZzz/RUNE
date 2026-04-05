package org.jellyfin.androidtv.ui.search

import android.content.Context
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.OnItemViewSelectedListener
import androidx.leanback.widget.Row
import org.jellyfin.androidtv.R
import org.jellyfin.androidtv.constant.QueryType
import org.jellyfin.androidtv.ui.itemhandling.BaseRowItem
import org.jellyfin.androidtv.ui.itemhandling.ItemLauncher
import org.jellyfin.androidtv.ui.itemhandling.ItemRowAdapter
import org.jellyfin.androidtv.ui.presentation.CardPresenter
import org.jellyfin.androidtv.ui.presentation.CustomListRowPresenter
import org.jellyfin.androidtv.ui.presentation.MutableObjectAdapter
import org.jellyfin.androidtv.ui.presentation.TextItemPresenter

class SearchFragmentDelegate(
	private val context: Context,
	private val itemLauncher: ItemLauncher,
) {
	val rowsAdapter = MutableObjectAdapter<Row>(CustomListRowPresenter())
	var onRecentQuerySelected: ((String) -> Unit)? = null

	fun showResults(
		searchResultGroups: Collection<SearchResultGroup>,
		recentQueries: List<String> = emptyList(),
	) {
		rowsAdapter.clear()
		val adapters = mutableListOf<ItemRowAdapter>()
		for (row in buildSearchRows(searchResultGroups, recentQueries)) {
			when (row) {
				is SearchRowModel.RecentQueries -> {
					val adapter = MutableObjectAdapter<String>(TextItemPresenter())
					row.queries.forEach(adapter::add)
					rowsAdapter.add(ListRow(HeaderItem(context.getString(R.string.lbl_recent_searches)), adapter))
				}
				is SearchRowModel.ResultGroup -> {
					val searchResultGroup = row.searchResultGroup
					val adapter = ItemRowAdapter(
						context,
						searchResultGroup.items.toList(),
						CardPresenter(),
						rowsAdapter,
						QueryType.Search
					).apply {
						setRow(ListRow(HeaderItem(context.getString(searchResultGroup.labelRes)), this))
					}
					adapters.add(adapter)
				}
			}
		}
		for (adapter in adapters) adapter.Retrieve()
	}

	val onItemViewClickedListener = OnItemViewClickedListener { _, item, _, row ->
		extractRecentQuery(item)?.let { query ->
			onRecentQuerySelected?.invoke(query)
			return@OnItemViewClickedListener
		}

		if (item !is BaseRowItem) return@OnItemViewClickedListener
		row as ListRow
		val adapter = row.adapter as ItemRowAdapter
		itemLauncher.launch(item as BaseRowItem?, adapter, context)
	}

	val onItemViewSelectedListener = OnItemViewSelectedListener { _, item, _, _ ->
		// Don't change background for search results to improve performance and UX
		// The background will remain static while browsing search results
	}
}
