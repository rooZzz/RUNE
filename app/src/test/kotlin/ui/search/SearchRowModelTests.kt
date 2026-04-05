package org.jellyfin.androidtv.ui.search

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import java.util.UUID

class SearchRowModelTests : FunSpec({
	test("recent row appears only when recent queries are provided") {
		val results = listOf(
			SearchResultGroup(
				labelRes = 1,
				items = listOf(
					BaseItemDto(
						id = UUID.randomUUID(),
						type = BaseItemKind.MOVIE,
						name = "Dune"
					)
				)
			)
		)

		val withRecent = buildSearchRows(results, recentQueries = listOf("batman"))
		withRecent.first().shouldBeInstanceOf<SearchRowModel.RecentQueries>()

		val withoutRecent = buildSearchRows(results, recentQueries = emptyList())
		withoutRecent.first().shouldBeInstanceOf<SearchRowModel.ResultGroup>()
	}

	test("recent row click extractor returns query for string item") {
		extractRecentQuery("batman") shouldBe "batman"
		extractRecentQuery(BaseItemDto(id = UUID.randomUUID(), type = BaseItemKind.MOVIE)) shouldBe null
	}

	test("content rows preserve original result-group order") {
		val firstGroup = SearchResultGroup(
			labelRes = 1,
			items = listOf(BaseItemDto(id = UUID.randomUUID(), type = BaseItemKind.MOVIE, name = "A"))
		)
		val secondGroup = SearchResultGroup(
			labelRes = 2,
			items = listOf(BaseItemDto(id = UUID.randomUUID(), type = BaseItemKind.SERIES, name = "B"))
		)

		val rows = buildSearchRows(listOf(firstGroup, secondGroup), recentQueries = listOf("x"))
		rows.drop(1).shouldContainExactly(
			SearchRowModel.ResultGroup(searchResultGroup = firstGroup),
			SearchRowModel.ResultGroup(searchResultGroup = secondGroup),
		)
	}
})
