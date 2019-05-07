package dev.olog.msc.core.interactor.last.fm

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.LastFmGateway
import dev.olog.msc.core.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class DeleteLastFmAlbumUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: LastFmGateway

): CompletableUseCaseWithParam<MediaId>(schedulers) {

    override fun buildUseCaseObservable(param: MediaId): Completable {
        return Completable.fromCallable {
            if (param.isPodcastAlbum){
                gateway.deletePodcastAlbum(param.resolveId)
            } else {
                gateway.deleteAlbum(param.resolveId)
            }

        }
    }
}