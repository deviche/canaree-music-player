package dev.olog.msc.presentation.dialogs.playlist.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.base.ViewModelKey
import dev.olog.msc.presentation.dialogs.playlist.NewPlaylistDialog
import dev.olog.msc.presentation.dialogs.playlist.NewPlaylistDialogViewModel


@Module
abstract class NewPlaylistDialogModule {

    @ContributesAndroidInjector
    abstract fun provideFragment(): NewPlaylistDialog

    @Binds
    @IntoMap
    @ViewModelKey(NewPlaylistDialogViewModel::class)
    abstract fun provideViewModel(viewModel: NewPlaylistDialogViewModel): ViewModel


}