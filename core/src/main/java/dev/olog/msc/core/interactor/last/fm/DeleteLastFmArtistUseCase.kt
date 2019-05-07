package dev.olog.msc.core.interactor.last.fm

import dev.olog.msc.core.MediaId
import dev.olog.msc.core.executors.IoScheduler
import dev.olog.msc.core.gateway.LastFmGateway
import dev.olog.msc.core.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class DeleteLastFmArtistUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: LastFmGateway

): CompletableUseCaseWithParam<MediaId>(schedulers) {

    override fun buildUseCaseObservable(param: MediaId): Completable {
        return Completable.fromCallable {
            if (param.isPodcastArtist){
                gateway.deletePodcastArtist(param.resolveId)
            } else {
                gateway.deleteArtist(param.resolveId)
            }

        }
    }
}