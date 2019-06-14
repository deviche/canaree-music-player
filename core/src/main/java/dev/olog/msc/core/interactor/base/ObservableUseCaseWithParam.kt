package dev.olog.msc.core.interactor.base

import dev.olog.msc.core.executor.Schedulers
import io.reactivex.Observable

abstract class ObservableUseCaseWithParam<T, in Param>(
        private val schedulers: Schedulers
) {

    protected abstract fun buildUseCaseObservable(param: Param): Observable<T>

    fun execute(param: Param): Observable<T> {
        return Observable.defer { this.buildUseCaseObservable(param)
                .subscribeOn(schedulers.worker)
                .observeOn(schedulers.ui) }
    }

}