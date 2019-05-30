package dev.olog.msc.presentation.tabs.paging.podcast

import dev.olog.msc.core.entity.data.request.Filter
import dev.olog.msc.core.entity.data.request.Request
import dev.olog.msc.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.base.list.paging.BaseDataSource
import dev.olog.msc.presentation.base.list.paging.BaseDataSourceFactory
import dev.olog.msc.presentation.tabs.TabFragmentHeaders
import dev.olog.msc.presentation.tabs.mapper.toTabDisplayableItem
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

internal class PodcastAlbumDataSource @Inject constructor(
    private val gateway: PodcastAlbumGateway,
    private val displayableHeaders: TabFragmentHeaders
) : BaseDataSource<DisplayableItem>() {

    private val chunked = gateway.getAll()

    override fun onAttach() {
        launch {
            chunked.observeNotification()
                .take(1)
                .collect {
                    invalidate()
                }
        }
    }

    override suspend fun getMainDataSize(): Int {
        return chunked.getCount(Filter.NO_FILTER)
    }

    override suspend fun getHeaders(mainListSize: Int): List<DisplayableItem> {
        val headers = loadParallel(
            async {
                if (gateway.canShowRecentlyAdded(Filter.NO_FILTER)) {
                    return@async displayableHeaders.recentlyAddedAlbumsHeaders
                }
                emptyList<DisplayableItem>()
            },
            async {
                if (gateway.canShowLastPlayed()) {
                    return@async displayableHeaders.lastPlayedAlbumHeaders
                }
                emptyList<DisplayableItem>()
            }
        )

        if (headers.isNotEmpty()) {
            return headers.plus(displayableHeaders.allAlbumsHeader)
        }
        return emptyList()
    }

    override suspend fun getFooters(mainListSize: Int): List<DisplayableItem> = listOf()

    override fun loadInternal(request: Request): List<DisplayableItem> {
        return chunked.getPage(request)
            .map { it.toTabDisplayableItem() }
    }

}

internal class PodcastAlbumDataSourceFactory @Inject constructor(
    dataSourceProvider: Provider<PodcastAlbumDataSource>
) : BaseDataSourceFactory<DisplayableItem, PodcastAlbumDataSource>(dataSourceProvider)