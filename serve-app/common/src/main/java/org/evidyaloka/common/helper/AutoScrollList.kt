package org.evidyaloka.common.helper

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.delay
import org.evidyaloka.common.interfaces.IAdapter

object AutoScrollList {
    private const val DELAY_BETWEEN_SCROLL_MS: Long = 5 * 1000
    private const val SCROLL_DX = 5
    private const val DIRECTION_RIGHT = 1

    public tailrec suspend fun <T> autoScrollFeaturesList(
        recyclerView: RecyclerView,
        adapter: IAdapter<T>
    ) {
        try {
            if (recyclerView.canScrollHorizontally(DIRECTION_RIGHT)) {
                val nextView =
                    ((recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() + 1) % (adapter as RecyclerView.Adapter<*>).itemCount
                recyclerView.smoothScrollToPosition(nextView)
            } else {
                recyclerView.smoothScrollToPosition(0)
            }
            delay(DELAY_BETWEEN_SCROLL_MS)
            autoScrollFeaturesList(recyclerView, adapter)
        } catch (e: Exception) {
        }
    }
}