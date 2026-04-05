package org.jellyfin.androidtv.ui.playback

import org.jellyfin.androidtv.preference.constant.AudioLanguage
import org.jellyfin.androidtv.preference.constant.SubtitleLanguage
import org.jellyfin.sdk.model.api.MediaStream
import org.jellyfin.sdk.model.api.MediaStreamType

object TrackSelection {

    @JvmStatic
    fun selectPreferredSubtitleStreamIndex(
        streams: List<MediaStream>?,
        preferredLanguage: SubtitleLanguage?,
        defaultSubtitleStreamIndex: Int?,
    ): Int? {
        return when (preferredLanguage) {
            null, SubtitleLanguage.NONE -> null
            SubtitleLanguage.DEFAULT -> defaultSubtitleStreamIndex
            else -> {
                if (streams.isNullOrEmpty()) {
                    defaultSubtitleStreamIndex
                } else {
                    val preferredCode = preferredLanguage.code
                    val subtitleStreams = streams.filter { it.type == MediaStreamType.SUBTITLE }
                    val candidates = subtitleStreams.filter { stream ->
                        val lang = stream.language ?: return@filter false
                        languageCodesMatch(preferredCode, lang)
                    }
                    when {
                        candidates.isEmpty() -> defaultSubtitleStreamIndex
                        else -> {
                            candidates.minWith(
                                compareBy<MediaStream> { if (it.isHearingImpaired == true) 1 else 0 }
                                    .thenBy { if (it.isDefault == true) 0 else 1 }
                                    .thenBy { it.index },
                            ).index
                        }
                    }
                }
            }
        }
    }

    @JvmStatic
    fun selectPreferredAudioStreamIndex(
        streams: List<MediaStream>?,
        preferredLanguageIso: String?,
        skipCommentaryTracks: Boolean,
    ): Int? {
        if (streams.isNullOrEmpty()) return null
        val normalized = preferredLanguageIso?.trim()?.lowercase()
        if (normalized.isNullOrEmpty()) return null

        val audioStreams = streams.filter { it.type == MediaStreamType.AUDIO }
        val candidates = audioStreams.filter { stream ->
            if (skipCommentaryTracks) {
                val title = stream.displayTitle?.lowercase() ?: ""
                if (title.contains("commentary")) return@filter false
            }
            val lang = stream.language ?: return@filter false
            languageCodesMatch(normalized, lang)
        }
        if (candidates.isEmpty()) return null
        return candidates.minWith(
            compareBy<MediaStream> { if (it.isHearingImpaired == true) 1 else 0 }
                .thenBy { it.index },
        ).index
    }

    internal fun languageCodesMatch(preferredIso: String, streamLanguage: String): Boolean {
        val p = preferredIso.trim().lowercase()
        val s = streamLanguage.trim().lowercase()
        if (p == s) return true
        val prefAudio = AudioLanguage.fromCode(p)
        val streamAudio = AudioLanguage.fromCode(s)
        if (prefAudio != null && streamAudio != null && prefAudio == streamAudio) return true
        return false
    }
}
