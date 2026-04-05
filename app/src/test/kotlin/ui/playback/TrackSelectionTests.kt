package org.jellyfin.androidtv.ui.playback

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.jellyfin.androidtv.preference.constant.SubtitleLanguage
import org.jellyfin.sdk.model.api.MediaStream
import org.jellyfin.sdk.model.api.MediaStreamType

class TrackSelectionTests : FunSpec({

	fun mockStream(
		type: MediaStreamType,
		index: Int,
		language: String?,
		isHearingImpaired: Boolean = false,
		isDefault: Boolean = false,
		displayTitle: String? = null,
	): MediaStream = mockk {
		every { this@mockk.type } returns type
		every { this@mockk.index } returns index
		every { this@mockk.language } returns language
		every { this@mockk.isHearingImpaired } returns isHearingImpaired
		every { this@mockk.isDefault } returns isDefault
		every { this@mockk.displayTitle } returns displayTitle
	}

	test("subtitle DEFAULT returns server default index without matching German deu") {
		val streams = listOf(
			mockStream(MediaStreamType.SUBTITLE, 1, "deu", isHearingImpaired = false),
			mockStream(MediaStreamType.SUBTITLE, 2, "eng", isHearingImpaired = false),
		)
		TrackSelection.selectPreferredSubtitleStreamIndex(
			streams,
			SubtitleLanguage.DEFAULT,
			2,
		) shouldBe 2
	}

	test("subtitle ENGLISH prefers non-hearing-impaired when both eng present") {
		val streams = listOf(
			mockStream(MediaStreamType.SUBTITLE, 1, "eng", isHearingImpaired = true),
			mockStream(MediaStreamType.SUBTITLE, 2, "eng", isHearingImpaired = false),
		)
		TrackSelection.selectPreferredSubtitleStreamIndex(
			streams,
			SubtitleLanguage.ENGLISH,
			null,
		) shouldBe 2
	}

	test("subtitle ENGLISH uses SDH when only hearing-impaired eng exists") {
		val streams = listOf(
			mockStream(MediaStreamType.SUBTITLE, 4, "eng", isHearingImpaired = true),
		)
		TrackSelection.selectPreferredSubtitleStreamIndex(
			streams,
			SubtitleLanguage.ENGLISH,
			null,
		) shouldBe 4
	}

	test("subtitle NONE returns null") {
		val streams = listOf(mockStream(MediaStreamType.SUBTITLE, 0, "eng"))
		TrackSelection.selectPreferredSubtitleStreamIndex(
			streams,
			SubtitleLanguage.NONE,
			0,
		) shouldBe null
	}

	test("subtitle no language match falls back to server default") {
		val streams = listOf(mockStream(MediaStreamType.SUBTITLE, 1, "fra"))
		TrackSelection.selectPreferredSubtitleStreamIndex(
			streams,
			SubtitleLanguage.ENGLISH,
			9,
		) shouldBe 9
	}

	test("audio preferred ja matches jpn stream over eng") {
		val streams = listOf(
			mockStream(MediaStreamType.AUDIO, 0, "eng", displayTitle = "Stereo English"),
			mockStream(MediaStreamType.AUDIO, 1, "jpn", displayTitle = "EAC3 5.1 - Japanese [JPN]"),
		)
		TrackSelection.selectPreferredAudioStreamIndex(streams, "ja", false) shouldBe 1
	}

	test("audio skips commentary track when skipCommentary true") {
		val streams = listOf(
			mockStream(
				MediaStreamType.AUDIO,
				0,
				"eng",
				displayTitle = "Commentary with Director",
			),
			mockStream(MediaStreamType.AUDIO, 1, "eng", displayTitle = "Main"),
		)
		TrackSelection.selectPreferredAudioStreamIndex(streams, "en", true) shouldBe 1
	}

	test("audio de matches deu stream") {
		val streams = listOf(
			mockStream(MediaStreamType.AUDIO, 0, "eng"),
			mockStream(MediaStreamType.AUDIO, 3, "deu", displayTitle = "AC3 5.1"),
		)
		TrackSelection.selectPreferredAudioStreamIndex(streams, "de", false) shouldBe 3
	}

	test("audio messy displayTitle still follows language field") {
		val streams = listOf(
			mockStream(
				MediaStreamType.AUDIO,
				0,
				"jpn",
				displayTitle = "TrueHD 7.1 Atmos - Japanese DTS-HD MA",
			),
		)
		TrackSelection.selectPreferredAudioStreamIndex(streams, "ja", false) shouldBe 0
	}
})
