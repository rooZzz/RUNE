package org.jellyfin.androidtv.ui.browsing

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.jellyfin.sdk.model.api.BaseItemKind
import java.util.UUID

class BrowsingUtilsNextEpisodesTests : FunSpec({
	test("creates next episode request for full season list") {
		val seasonId = UUID.randomUUID()
		val request = BrowsingUtils.createNextEpisodesRequest(seasonId = seasonId)

		request.parentId shouldBe seasonId
		request.startIndex shouldBe null
		request.limit shouldBe null
		request.includeItemTypes shouldBe setOf(BaseItemKind.EPISODE)
	}

	test("does not change series next up request behavior") {
		val seriesId = UUID.randomUUID()
		val request = BrowsingUtils.createSeriesGetNextUpRequest(seriesId)

		request.seriesId shouldBe seriesId
		request.parentId shouldBe null
	}
})
