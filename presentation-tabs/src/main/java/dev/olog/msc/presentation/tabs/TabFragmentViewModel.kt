package dev.olog.msc.presentation.tabs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dev.olog.msc.core.MediaIdCategory
import dev.olog.msc.core.entity.sort.LibrarySortType
import dev.olog.msc.core.gateway.prefs.SortPreferencesGateway
import dev.olog.msc.presentation.base.list.model.DisplayableItem
import dev.olog.msc.presentation.tabs.paging.last.played.LastPlayedAlbumDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.last.played.LastPlayedArtistDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.last.played.LastPlayedPodcastAlbumDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.last.played.LastPlayedPodcastArtistDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.podcast.PodcastAlbumDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.podcast.PodcastArtistDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.podcast.PodcastDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.podcast.PodcastPlaylistDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.recently.added.RecentlyAddedAlbumDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.recently.added.RecentlyAddedArtistDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.recently.added.RecentlyAddedPodcastAlbumDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.recently.added.RecentlyAddedPodcastArtistDataSourceFactory
import dev.olog.msc.presentation.tabs.paging.track.*
import kotlinx.coroutines.cancel
import javax.inject.Inject

internal class TabFragmentViewModel @Inject constructor(
    private val sortPrefs: SortPreferencesGateway,
    // tracks
    private val folderDataSource: FolderDataSourceFactory,
    private val playlistDataSource: PlaylistDataSourceFactory,
    private val songDataSource: SongDataSourceFactory,
    private val albumDataSource: AlbumDataSourceFactory,
    private val artistDataSource: ArtistDataSourceFactory,
    private val genreDataSource: GenreDataSourceFactory,
    private val lastPlayedArtistDataSource: LastPlayedArtistDataSourceFactory,
    private val lastPlayedAlbumDataSource: LastPlayedAlbumDataSourceFactory,
    private val recentlyAddedArtistDataSource: RecentlyAddedArtistDataSourceFactory,
    private val recentlyAddedAlbumDataSource: RecentlyAddedAlbumDataSourceFactory,
    // podcast
    private val podcastDataSource: PodcastDataSourceFactory,
    private val podcastPlaylistDataSource: PodcastPlaylistDataSourceFactory,
    private val podcastAlbumDataSource: PodcastAlbumDataSourceFactory,
    private val podcastArtistDataSource: PodcastArtistDataSourceFactory,
    private val lastPlayedPodcastArtistDataSource: LastPlayedPodcastArtistDataSourceFactory,
    private val lastPlayedPodcastAlbumDataSource: LastPlayedPodcastAlbumDataSourceFactory,
    private val recentlyAddedPodcastArtistDataSource: RecentlyAddedPodcastArtistDataSourceFactory,
    private val recentlyAddedPodcastAlbumDataSource: RecentlyAddedPodcastAlbumDataSourceFactory

) : ViewModel() {

    private val liveDataList: MutableMap<MediaIdCategory, LiveData<PagedList<DisplayableItem>>> = mutableMapOf()

    fun observeData(category: MediaIdCategory): LiveData<PagedList<DisplayableItem>> {
        return liveDataList.getOrPut(category) {
            val isLongList = category == MediaIdCategory.SONGS || category == MediaIdCategory.PODCASTS
            val config = PagedList.Config.Builder()
                .setPageSize(if (isLongList) 30 else 15)
                .setEnablePlaceholders(true)
                .build()
            LivePagedListBuilder(getFactory(category), config).build()
        }
    }

    private fun getFactory(category: MediaIdCategory): DataSource.Factory<Int, DisplayableItem> {
        return when (category) {
            // tracks
            MediaIdCategory.FOLDERS -> folderDataSource
            MediaIdCategory.PLAYLISTS -> playlistDataSource
            MediaIdCategory.SONGS -> songDataSource
            MediaIdCategory.ALBUMS -> albumDataSource
            MediaIdCategory.ARTISTS -> artistDataSource
            MediaIdCategory.GENRES -> genreDataSource
            MediaIdCategory.LAST_PLAYED_ARTISTS -> lastPlayedArtistDataSource
            MediaIdCategory.LAST_PLAYED_ALBUMS -> lastPlayedAlbumDataSource
            MediaIdCategory.RECENTLY_ADDED_ARTISTS -> recentlyAddedArtistDataSource
            MediaIdCategory.RECENTLY_ADDED_ALBUMS -> recentlyAddedAlbumDataSource
            // podcasts
            MediaIdCategory.PODCASTS_PLAYLIST -> podcastPlaylistDataSource
            MediaIdCategory.PODCASTS -> podcastDataSource
            MediaIdCategory.PODCASTS_ALBUMS -> podcastAlbumDataSource
            MediaIdCategory.PODCASTS_ARTISTS -> podcastArtistDataSource
            MediaIdCategory.LAST_PLAYED_PODCAST_ALBUMS -> lastPlayedPodcastAlbumDataSource
            MediaIdCategory.LAST_PLAYED_PODCAST_ARTISTS -> lastPlayedPodcastArtistDataSource
            MediaIdCategory.RECENTLY_ADDED_PODCAST_ALBUMS -> recentlyAddedPodcastAlbumDataSource
            MediaIdCategory.RECENTLY_ADDED_PODCAST_ARTISTS -> recentlyAddedPodcastArtistDataSource
            else -> throw IllegalArgumentException("invalid media category $category")
        }
    }

    fun getAllTracksSortOrder(): LibrarySortType {
        return sortPrefs.getAllTracksSortOrder()
    }

    fun getAllAlbumsSortOrder(): LibrarySortType {
        return sortPrefs.getAllAlbumsSortOrder()
    }

    override fun onCleared() {
        viewModelScope.cancel()
        // track
        folderDataSource.onDetach()
        playlistDataSource.onDetach()
        songDataSource.onDetach()
        albumDataSource.onDetach()
        artistDataSource.onDetach()
        genreDataSource.onDetach()
        lastPlayedArtistDataSource.onDetach()
        lastPlayedAlbumDataSource.onDetach()
        recentlyAddedArtistDataSource.onDetach()
        recentlyAddedAlbumDataSource.onDetach()
        // podcast
        podcastPlaylistDataSource.onDetach()
        podcastDataSource.onDetach()
        podcastAlbumDataSource.onDetach()
        podcastArtistDataSource.onDetach()
        lastPlayedPodcastArtistDataSource.onDetach()
        lastPlayedPodcastAlbumDataSource.onDetach()
        recentlyAddedPodcastArtistDataSource.onDetach()
        recentlyAddedPodcastAlbumDataSource.onDetach()
    }

}