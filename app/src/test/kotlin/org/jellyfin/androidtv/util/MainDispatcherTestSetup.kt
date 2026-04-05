package org.jellyfin.androidtv.util

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
fun FunSpec.configureMainDispatcher(
	dispatcherFactory: () -> TestDispatcher = { StandardTestDispatcher() }
) {
	lateinit var dispatcher: TestDispatcher

	beforeTest {
		dispatcher = dispatcherFactory()
		Dispatchers.setMain(dispatcher)
	}

	afterTest {
		Dispatchers.resetMain()
	}
}
