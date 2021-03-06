package dev.olog.presentation.dialogs.playlist.duplicates

import dev.olog.core.MediaId
import dev.olog.core.interactor.playlist.RemoveDuplicatesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoveDuplicatesDialogPresenter @Inject constructor(
    private val useCase: RemoveDuplicatesUseCase
) {

    suspend fun execute(mediaId: MediaId) = withContext(Dispatchers.IO){
        useCase(mediaId)
    }

}