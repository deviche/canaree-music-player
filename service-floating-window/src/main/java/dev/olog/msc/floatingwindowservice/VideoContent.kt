package dev.olog.msc.floatingwindowservice

import android.content.Context
import androidx.lifecycle.Lifecycle

internal class VideoContent(
        lifecycle: Lifecycle,
        context: Context

) : WebViewContent(lifecycle, context, R.layout.content_web_view) {

    override fun getUrl(item: String): String {
        return "https://www.youtube.com/search?q=$item"
    }

}