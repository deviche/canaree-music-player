package dev.olog.presentation.edit.song

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import dev.olog.presentation.R
import dev.olog.shared.AppConstants
import dev.olog.presentation.edit.BaseEditItemFragment
import dev.olog.presentation.edit.EditItemViewModel
import dev.olog.presentation.edit.UpdateResult
import dev.olog.presentation.edit.UpdateSongInfo
import dev.olog.core.MediaId
import dev.olog.shared.extensions.*
import kotlinx.android.synthetic.main.fragment_edit_track.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditTrackFragment : BaseEditItemFragment(), CoroutineScope by MainScope() {

    companion object {
        const val TAG = "EditTrackFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"

        @JvmStatic
        fun newInstance(mediaId: MediaId): EditTrackFragment {
            return EditTrackFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString()
            )
        }
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazyFast {
        viewModelProvider<EditTrackFragmentViewModel>(
            viewModelFactory
        )
    }
    private val editItemViewModel by lazyFast {
        activity!!.viewModelProvider<EditItemViewModel>(
            viewModelFactory
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        viewModel.observeData().observe(this, Observer {
//            if (it == null){
//                ctx.toast(R.string.edit_song_info_not_found)
//            } else {
//                title.setText(it.title)
//                artist.setText(it.artist)
//                albumArtist.setText(it.albumArtist)
//                album.setText(it.album)
//                year.setText(it.year)
//                genre.setText(it.genre)
//                disc.setText(it.disc)
//                trackNumber.setText(it.track)
//                bitrate.text = it.bitrate
//                format.text = it.format
//                sampling.text = it.sampling
//                setImage(MediaId.songId(it.id)) TODO
//            }
//            hideLoader()
//        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        launch {
            title.afterTextChange()
                .map { it.isNotBlank() }
                .collect { okButton.isEnabled = it }
        }
    }

    override fun onResume() {
        super.onResume()
        okButton.setOnClickListener {
            launch {
                val result = editItemViewModel.updateSong(
                    UpdateSongInfo(
                        viewModel.getSong(),
                        title.extractText().trim(),
                        artist.extractText().trim(),
                        albumArtist.extractText().trim(),
                        album.extractText().trim(),
                        genre.extractText().trim(),
                        year.extractText().trim(),
                        disc.extractText().trim(),
                        trackNumber.extractText().trim(),
                        viewModel.getNewImage()
                    )
                )

                when (result){
                    UpdateResult.OK -> dismiss()
                    UpdateResult.EMPTY_TITLE -> ctx.toast(R.string.edit_song_invalid_title)
                    UpdateResult.ILLEGAL_DISC_NUMBER -> ctx.toast(R.string.edit_song_invalid_disc_number)
                    UpdateResult.ILLEGAL_TRACK_NUMBER -> ctx.toast(R.string.edit_song_invalid_track_number)
                    UpdateResult.ILLEGAL_YEAR -> ctx.toast(R.string.edit_song_invalid_year)
                }
            }
        }
        cancelButton.setOnClickListener { dismiss() }
        autoTag.setOnClickListener {
            if (viewModel.fetchSongInfo()) {
                showLoader(R.string.edit_song_fetching_info)
            } else {
                ctx.toast(R.string.common_no_internet)
            }
        }
        picker.setOnClickListener { changeImage() }
    }

    override fun onPause() {
        super.onPause()
        okButton.setOnClickListener(null)
        cancelButton.setOnClickListener(null)
        autoTag.setOnClickListener(null)
        picker.setOnClickListener(null)
    }

    override fun onImagePicked(uri: Uri) {
        viewModel.updateImage(uri.toString())
    }

    override fun restoreImage() {
//        val albumId = viewModel.getSong().albumId
//        val uri = ImagesFolderUtils.forAlbum(albumId)
//        viewModel.updateImage(uri) TODO
    }

    override fun noImage() {
        viewModel.updateImage(AppConstants.NO_IMAGE)
    }

    override fun onLoaderCancelled() {
        viewModel.stopFetching()
    }

    override fun provideLayoutId(): Int = R.layout.fragment_edit_track
}