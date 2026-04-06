package org.jellyfin.androidtv.ui.itemhandling

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import java.util.UUID

class NextEpisodeSelectionIndexTests : FunSpec({
	test("returns current episode index when item id exists in season list") {
		val episode1 = episode()
		val episode2 = episode()
		val episode3 = episode()

		findInitialSelectionIndexByItemId(
			items = listOf(episode1, episode2, episode3),
			selectedItemId = episode2.id,
		) shouldBe 1
	}

	test("returns -1 when selected item id is not in season list") {
		findInitialSelectionIndexByItemId(
			items = listOf(episode(), episode()),
			selectedItemId = UUID.randomUUID(),
		) shouldBe -1
	}

	test("returns -1 when selected item id is null") {
		findInitialSelectionIndexByItemId(
			items = listOf(episode(), episode()),
			selectedItemId = null,
		) shouldBe -1
	}
})

private fun episode() = BaseItemDto(
	id = UUID.randomUUID(),
	type = BaseItemKind.EPISODE,
)
