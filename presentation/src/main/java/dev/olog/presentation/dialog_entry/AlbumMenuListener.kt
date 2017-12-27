package dev.olog.presentation.dialog_entry

import android.app.Application
import android.view.MenuItem
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.item.GetAlbumUseCase
import dev.olog.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.domain.interactor.dialog.GetPlaylistBlockingUseCase
import dev.olog.presentation.R
import dev.olog.presentation.navigation.Navigator
import dev.olog.shared.MediaIdHelper
import javax.inject.Inject

class AlbumMenuListener @Inject constructor(
        application: Application,
        getSongListByParamUseCase: GetSongListByParamUseCase,
        private val navigator: Navigator,
        private val getAlbumUseCase: GetAlbumUseCase,
        getPlaylistBlockingUseCase: GetPlaylistBlockingUseCase,
        addToPlaylistUseCase: AddToPlaylistUseCase

) : BaseMenuListener(application, getSongListByParamUseCase, navigator,
        getPlaylistBlockingUseCase, addToPlaylistUseCase) {

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId
        when (itemId){
            R.id.viewArtist -> {
                getAlbumUseCase.execute(item.mediaId)
                        .map { MediaIdHelper.artistId(it.artistId) }
                        .firstOrError()
                        .doOnSuccess { navigator.toDetailFragment(it) }
                        .toCompletable()
                        .subscribe()
                return true
            }
        }
        return super.onMenuItemClick(menuItem)
    }

}
