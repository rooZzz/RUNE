package org.jellyfin.androidtv.ui.presentation

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.Presenter
import android.text.TextUtils

class TextItemPresenter : Presenter() {
	companion object {
		private const val ITEM_WIDTH_DP = 320
		private const val ITEM_HEIGHT_DP = 88
		private const val ITEM_HORIZONTAL_PADDING_DP = 20
		private const val TEXT_SIZE = 18f
	}

	override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
		val density = parent.context.resources.displayMetrics.density
		val itemWidth = (ITEM_WIDTH_DP * density).toInt()
		val itemHeight = (ITEM_HEIGHT_DP * density).toInt()
		val horizontalPadding = (ITEM_HORIZONTAL_PADDING_DP * density).toInt()
		val view = TextView(parent.context).apply {
			layoutParams = ViewGroup.LayoutParams(itemWidth, itemHeight)
			isFocusable = true
			isFocusableInTouchMode = true
			setTextColor(Color.WHITE)
			gravity = Gravity.START or Gravity.CENTER_VERTICAL
			setPadding(horizontalPadding, 0, horizontalPadding, 0)
			textSize = TEXT_SIZE
			maxLines = 1
			ellipsize = TextUtils.TruncateAt.END
		}

		return ViewHolder(view)
	}

    // This version is called by the framework with payloads
    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any, payloads: MutableList<Any>) {
        onBindViewHolder(viewHolder, item)
    }

    // Main implementation that handles binding
    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        (viewHolder.view as? TextView)?.text = item?.toString() ?: ""
    }

    @Suppress("UNUSED_PARAMETER")
    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        // No cleanup needed
    }
}
