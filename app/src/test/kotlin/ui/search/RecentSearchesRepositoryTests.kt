package org.jellyfin.androidtv.ui.search

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class RecentSearchesRepositoryTests : FunSpec({
	class FakeStorage : RecentSearchesStorage {
		var raw: String = ""

		override fun read(): String = raw

		override fun write(value: String) {
			raw = value
		}
	}

	var currentUserId: String? = "user-1"
	val storage = FakeStorage()
	val repository = RecentSearchesRepositoryImpl(
		storage = storage,
		currentUserIdProvider = { currentUserId },
		maxEntries = 3,
		maxQueryLength = 50
	)

	beforeEach {
		currentUserId = "user-1"
		storage.raw = ""
	}

	test("stores query for current user key") {
		repository.addRecentSearch("batman")
		repository.getRecentSearches() shouldContainExactly listOf("batman")
	}

	test("returns newest-first order") {
		repository.addRecentSearch("batman")
		repository.addRecentSearch("dune")
		repository.addRecentSearch("silo")

		repository.getRecentSearches() shouldContainExactly listOf("silo", "dune", "batman")
	}

	test("deduplicates existing query case-insensitively and moves it to top") {
		repository.addRecentSearch("Batman")
		repository.addRecentSearch("dune")
		repository.addRecentSearch("batman")

		repository.getRecentSearches() shouldContainExactly listOf("batman", "dune")
	}

	test("enforces max size") {
		repository.addRecentSearch("one")
		repository.addRecentSearch("two")
		repository.addRecentSearch("three")
		repository.addRecentSearch("four")

		repository.getRecentSearches() shouldContainExactly listOf("four", "three", "two")
	}

	test("ignores blank and whitespace queries") {
		repository.addRecentSearch("")
		repository.addRecentSearch("   ")
		repository.addRecentSearch("dune")

		repository.getRecentSearches() shouldContainExactly listOf("dune")
	}

	test("isolates data between users") {
		repository.addRecentSearch("batman")

		currentUserId = "user-2"
		repository.getRecentSearches() shouldBe emptyList<String>()
		repository.addRecentSearch("silo")
		repository.getRecentSearches() shouldContainExactly listOf("silo")

		currentUserId = "user-1"
		repository.getRecentSearches() shouldContainExactly listOf("batman")
	}

	test("app relaunch keeps recents for the same user") {
		repository.addRecentSearch("foundation")

		val recreatedRepository = RecentSearchesRepositoryImpl(
			storage = storage,
			currentUserIdProvider = { currentUserId },
			maxEntries = 3,
			maxQueryLength = 50
		)
		recreatedRepository.getRecentSearches() shouldContainExactly listOf("foundation")
	}

	test("switching users returns user-specific recents immediately") {
		repository.addRecentSearch("silo")

		currentUserId = "user-2"
		repository.addRecentSearch("dune")
		repository.getRecentSearches() shouldContainExactly listOf("dune")

		currentUserId = "user-1"
		repository.getRecentSearches() shouldContainExactly listOf("silo")
	}

	test("malformed stored payload falls back to empty and recovers on next write") {
		storage.raw = "{not-valid-json"
		repository.getRecentSearches() shouldContainExactly emptyList<String>()

		repository.addRecentSearch("batman")
		repository.getRecentSearches() shouldContainExactly listOf("batman")
	}

	test("extremely long query is truncated to max length") {
		val longQuery = "a".repeat(200)
		repository.addRecentSearch(longQuery)

		repository.getRecentSearches().first().length shouldBe 50
	}
})
