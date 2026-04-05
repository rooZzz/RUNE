package org.jellyfin.androidtv.ui.search

import app.cash.turbine.test
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.jellyfin.androidtv.util.configureMainDispatcher
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelRecentSearchesTests : FunSpec({
	configureMainDispatcher()

	class FakeSearchRepository : SearchRepository {
		var responseDelayMs: Long = 0
		val calledQueries = mutableListOf<String>()

		override suspend fun search(
			searchTerm: String,
			itemTypes: Collection<BaseItemKind>
		): Result<List<BaseItemDto>> {
			calledQueries += searchTerm
			delay(responseDelayMs)
			return Result.success(
				listOf(
					BaseItemDto(
						id = UUID.randomUUID(),
						name = searchTerm,
						type = itemTypes.firstOrNull() ?: BaseItemKind.MOVIE
					)
				)
			)
		}
	}

	class FakeRecentSearchesRepository : RecentSearchesRepository {
		val addedQueries = mutableListOf<String>()
		var recents = listOf<String>()

		override fun getRecentSearches(): List<String> = recents

		override fun addRecentSearch(query: String) {
			addedQueries += query
		}

		override fun clearRecentSearches() = Unit
	}

	test("empty query emits recent searches") {
		val searchRepository = FakeSearchRepository()
		val recentSearchesRepository = FakeRecentSearchesRepository().apply {
			recents = listOf("batman", "dune")
		}
		val viewModel = SearchViewModel(searchRepository, recentSearchesRepository)

		runTest {
			viewModel.recentSearchesFlow.test {
				awaitItem() shouldContainExactly listOf("batman", "dune")
			}
		}
	}

	test("non-empty query hides recents") {
		val searchRepository = FakeSearchRepository()
		val recentSearchesRepository = FakeRecentSearchesRepository().apply {
			recents = listOf("batman")
		}
		val viewModel = SearchViewModel(searchRepository, recentSearchesRepository)

		runTest {
			viewModel.recentSearchesFlow.test {
				awaitItem() shouldContainExactly listOf("batman")
				viewModel.searchDebounced("dune", 0.milliseconds) shouldBe true
				awaitItem() shouldBe emptyList()
			}
		}
	}

	test("non-empty query emits grouped remote results") {
		val viewModel = SearchViewModel(FakeSearchRepository(), FakeRecentSearchesRepository())

		runTest {
			viewModel.searchResultsFlow.test {
				awaitItem() shouldBe emptyList()
				viewModel.searchDebounced("dune", 0.milliseconds) shouldBe true
				val groupedResults = awaitItem()
				groupedResults.isNotEmpty().shouldBeTrue()
				groupedResults.first().items.first().name shouldBe "dune"
			}
		}
	}

	test("submit search records query in recents and executes immediate search") {
		val searchRepository = FakeSearchRepository()
		val recentSearchesRepository = FakeRecentSearchesRepository()
		val viewModel = SearchViewModel(searchRepository, recentSearchesRepository)

		runTest {
			viewModel.submitSearch("silo")
			advanceUntilIdle()
		}

		recentSearchesRepository.addedQueries shouldContainExactly listOf("silo")
		searchRepository.calledQueries.contains("silo").shouldBeTrue()
	}

	test("duplicate query short-circuit keeps recent-search visibility stable") {
		val searchRepository = FakeSearchRepository()
		val recentSearchesRepository = FakeRecentSearchesRepository().apply {
			recents = listOf("fallout")
		}
		val viewModel = SearchViewModel(searchRepository, recentSearchesRepository)

		runTest {
			viewModel.searchDebounced("", 0.milliseconds) shouldBe true
			viewModel.searchDebounced("", 0.milliseconds) shouldBe false
			viewModel.recentSearchesFlow.value shouldContainExactly listOf("fallout")
		}
	}

	test("rapid query changes cancel prior search and keep latest results") {
		val searchRepository = FakeSearchRepository().apply {
			responseDelayMs = 50
		}
		val viewModel = SearchViewModel(searchRepository, FakeRecentSearchesRepository())

		runTest {
			viewModel.searchDebounced("ba", 0.milliseconds)
			viewModel.searchDebounced("batman", 0.milliseconds)
			advanceUntilIdle()
		}

		viewModel.searchResultsFlow.value.first().items.first().name shouldBe "batman"
	}
})
