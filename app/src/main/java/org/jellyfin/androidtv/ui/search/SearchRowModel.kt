package org.jellyfin.androidtv.ui.search

sealed interface SearchRowModel {
	data class RecentQueries(val queries: List<String>) : SearchRowModel
	data class ResultGroup(val searchResultGroup: SearchResultGroup) : SearchRowModel
}

fun buildSearchRows(
	searchResultGroups: Collection<SearchResultGroup>,
	recentQueries: List<String>,
): List<SearchRowModel> {
	val rows = mutableListOf<SearchRowModel>()
	if (recentQueries.isNotEmpty()) {
		rows += SearchRowModel.RecentQueries(recentQueries)
	}
	rows += searchResultGroups.map(SearchRowModel::ResultGroup)
	return rows
}

fun extractRecentQuery(item: Any?): String? = item as? String
