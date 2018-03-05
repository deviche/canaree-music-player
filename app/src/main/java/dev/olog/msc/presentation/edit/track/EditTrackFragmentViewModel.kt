package dev.olog.msc.presentation.edit.track

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import androidx.text.isDigitsOnly
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.presentation.NetworkConnectionPublisher
import dev.olog.msc.presentation.edit.UpdateResult
import dev.olog.msc.presentation.edit.track.model.DisplayableSong
import dev.olog.msc.utils.exception.AbsentNetwork
import dev.olog.msc.utils.k.extension.context
import dev.olog.msc.utils.k.extension.unsubscribe
import dev.olog.msc.utils.media.store.notifyMediaStore
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagOptionSingleton
import java.io.File

class EditTrackFragmentViewModel(
        application: Application,
        private val connectionPublisher: NetworkConnectionPublisher,
        private val presenter: EditTrackFragmentPresenter

) : AndroidViewModel(application) {

    private val displayedImage = MutableLiveData<String>()
    private val displayedSong = MutableLiveData<DisplayableSong>()

    private var getSongDisposable : Disposable? = null

    private var fetchSongInfoDisposable: Disposable? = null
    private var fetchAlbumImageDisposable: Disposable? = null

    init {
        TagOptionSingleton.getInstance().isAndroid = true

        getSongDisposable = presenter.getSong()
                .subscribe({
                    val song = it.toDisplayableSong()
                    displayedSong.postValue(song)
                    displayedImage.postValue(it.image)
                }, Throwable::printStackTrace)
    }

    fun observeData(): LiveData<DisplayableSong> = displayedSong
    fun observeImage(): LiveData<String> = displayedImage

    fun observeConnectivity() : Observable<String> = connectionPublisher.observe()

    fun getSongId(): Int = presenter.getId()

    fun fetchSongInfo(){
        fetchSongInfoDisposable.unsubscribe()
        fetchSongInfoDisposable = presenter.fetchData()
                .subscribe({ newValue ->
                    val oldValue = displayedSong.value!!
                    displayedSong.postValue(oldValue.copy(
                            title = newValue.title,
                            artist = newValue.artist,
                            album = newValue.album
                    ))
                }, {
                    if (it is AbsentNetwork){
                        connectionPublisher.next()
                    }
                    it.printStackTrace()
                    displayedSong.postValue(null)
                })
    }

    fun fetchAlbumArt() {
        fetchAlbumImageDisposable.unsubscribe()
        fetchAlbumImageDisposable = presenter.fetchData()
                .map { it.image }
                .subscribe({
                    displayedImage.postValue(it)
                }, {
                    if (it is AbsentNetwork){
                        connectionPublisher.next()
                    }
                    displayedImage.postValue(null)
                    it.printStackTrace()
                })
    }

    fun setAlbumArt(uri: String){
        displayedImage.postValue(uri)
    }

    fun restoreAlbumArt() {
        val originalImage = presenter.getOriginalImage()
        displayedImage.postValue(originalImage)
    }

    override fun onCleared() {
        getSongDisposable.unsubscribe()
        stopFetching()
    }

    fun stopFetching(){
        fetchSongInfoDisposable.unsubscribe()
        fetchAlbumImageDisposable.unsubscribe()
    }

    fun updateMetadata(
            title: String,
            artist: String,
            album: String,
            genre: String,
            year: String,
            disc: String,
            track: String

    ): UpdateResult {
        when {
            title.isBlank() -> return UpdateResult.EMPTY_TITLE
            year.isNotBlank() && !year.isDigitsOnly() -> return UpdateResult.ILLEGAL_YEAR
            disc.isNotBlank() && !disc.isDigitsOnly() -> return UpdateResult.ILLEGAL_DISC_NUMBER
            track.isNotBlank() && !track.isDigitsOnly() -> return UpdateResult.ILLEGAL_TRACK_NUMBER
        }

        try {
            presenter.updateSong(title, artist, album, genre, year, disc, track)
            presenter.updateUsedImage(displayedImage.value!!)
            notifyMediaStore(context, presenter.getPath())

            return UpdateResult.OK
        } catch (ex: Exception){
            ex.printStackTrace()
            return UpdateResult.ERROR
        }
    }

    private fun Song.toDisplayableSong(): DisplayableSong {
        val file = File(path)
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tagOrCreateAndSetDefault

        return DisplayableSong(
                this.id,
                this.title,
                artist,
                album,
                tag.getFirst(FieldKey.GENRE) ?: "",
                tag.getFirst(FieldKey.YEAR) ?: "",
                tag.getFirst(FieldKey.DISC_NO) ?: "",
                tag.getFirst(FieldKey.TRACK) ?: ""
        )
    }

}