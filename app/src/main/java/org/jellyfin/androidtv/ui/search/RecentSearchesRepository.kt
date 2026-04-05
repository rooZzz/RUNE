package org.jellyfin.androidtv.ui.search

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.jellyfin.androidtv.preference.UserPreferences

interface RecentSearchesStorage {
	fun read(): String
	fun write(value: String)
}

class UserPreferenceRecentSearchesStorage(
	private val userPreferences: UserPreferences
) : RecentSearchesStorage {
	override fun read(): String = userPreferences[UserPreferences.recentSearchesByUser]

	override fun write(value: String) {
		userPreferences[UserPreferences.recentSearchesByUser] = value
	}
}

interface RecentSearchesRepository {
	fun getRecentSearches(): List<String>
	fun addRecentSearch(query: String)
	fun clearRecentSearches()
}

class RecentSearchesRepositoryImpl(
	private val storage: RecentSearchesStorage,
	private val currentUserIdProvider: () -> String?,
	private val maxEntries: Int = DEFAULT_MAX_ENTRIES,
	private val maxQueryLength: Int = DEFAULT_MAX_QUERY_LENGTH,
) : RecentSearchesRepository {
	private val json = Json {
		ignoreUnknownKeys = true
	}

	override fun getRecentSearches(): List<String> {
		val userId = currentUserIdProvider()?.takeIf { it.isNotBlank() } ?: return emptyList()
		return decodeMap()[userId].orEmpty()
	}

	override fun addRecentSearch(query: String) {
		val userId = currentUserIdProvider()?.takeIf { it.isNotBlank() } ?: return
		val sanitizedQuery = sanitize(query) ?: return
		val current = decodeMap().toMutableMap()
		val existing = current[userId].orEmpty()
		val deduped = existing
			.filterNot { it.equals(sanitizedQuery, ignoreCase = true) }
			.toMutableList()
		deduped.add(0, sanitizedQuery)
		current[userId] = deduped.take(maxEntries.coerceAtLeast(1))
		encodeMap(current)
	}

	override fun clearRecentSearches() {
		val userId = currentUserIdProvider()?.takeIf { it.isNotBlank() } ?: return
		val current = decodeMap().toMutableMap()
		current.remove(userId)
		encodeMap(current)
	}

	private fun sanitize(value: String): String? {
		val trimmed = value.trim()
		if (trimmed.isBlank()) return null
		return trimmed.take(maxQueryLength.coerceAtLeast(1))
	}

	private fun decodeMap(): Map<String, List<String>> {
		val raw = storage.read().trim()
		if (raw.isEmpty()) return emptyMap()
		return try {
			json.decodeFromString(raw)
		} catch (_: SerializationException) {
			emptyMap()
		} catch (_: IllegalArgumentException) {
			emptyMap()
		}
	}

	private fun encodeMap(value: Map<String, List<String>>) {
		storage.write(json.encodeToString(value))
	}

	companion object {
		const val DEFAULT_MAX_ENTRIES = 10
		const val DEFAULT_MAX_QUERY_LENGTH = 120
	}
}
