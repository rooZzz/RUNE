package org.jellyfin.androidtv.ui.itemhandling

import android.content.Context
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.jellyfin.androidtv.ui.presentation.MutableObjectAdapter
import org.jellyfin.sdk.model.api.request.GetItemsRequest

class ItemRowAdapterSelectionStateTests : FunSpec({
	fun createAdapter(): ItemRowAdapter {
		val context = mockk<Context>(relaxed = true)
		val presenter = mockk<Presenter>(relaxed = true)
		val parent = mockk<MutableObjectAdapter<Row>>(relaxed = true)
		return ItemRowAdapter(
			context,
			GetItemsRequest(),
			0,
			false,
			presenter,
			parent,
		)
	}

	test("peek keeps index pending until applied") {
		val adapter = createAdapter()

		adapter.setInitialSelectionIndex(3)
		adapter.peekInitialSelectionIndex() shouldBe 3

		adapter.markInitialSelectionApplied()
		adapter.peekInitialSelectionIndex() shouldBe -1
	}

	test("consume supports focus-entry fallback behavior") {
		val adapter = createAdapter()

		adapter.setInitialSelectionIndex(2)
		adapter.consumeInitialSelectionIndex() shouldBe 2
		adapter.peekInitialSelectionIndex() shouldBe -1
	}

	test("listener fires when valid index becomes available") {
		val adapter = createAdapter()
		var callbackIndex = -1

		adapter.setSelectionIndexListener { callbackIndex = it }
		adapter.setInitialSelectionIndex(5)

		callbackIndex shouldBe 5
	}

	test("listener does not fire when index stays pending at -1") {
		val adapter = createAdapter()
		var callbackIndex = -1

		adapter.setSelectionIndexListener { callbackIndex = it }
		adapter.setInitialSelectionIndex(-1)

		callbackIndex shouldBe -1
	}
})
