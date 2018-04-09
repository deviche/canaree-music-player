package dev.olog.msc.presentation.widget.fast.scroller

import android.content.Context
import android.util.AttributeSet
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.runOnMainThread

class RxWaveSideBarView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : WaveSideBarView(context, attrs, defStyleAttr) {

    var scrollableLayoutId : Int = 0

    fun onDataChanged(list: List<DisplayableItem>){
        if (AppConstants.useFakeData){
            updateLetters(LETTERS)
        } else {
            updateLetters(generateLetters(list))
        }
    }

    fun setListener(listener: OnTouchLetterChangeListener?){
        this.listener = listener
    }

    private fun generateLetters(data: List<DisplayableItem>): List<String> {
        if (scrollableLayoutId == 0){
            throw IllegalStateException("provide a real layout id to filter")
        }

        val list = data.asSequence()
                .filter { it.type == scrollableLayoutId }
                .map { it.title[0].toUpperCase() }
                .distinctBy { it }
                .map { it.toString() }
                .toList()

        val letters = LETTERS.map { letter -> list.firstOrNull { it == letter } ?: TextUtils.MIDDLE_DOT }
                .toMutableList()
        list.firstOrNull { it < "A" }?.let { letters[0] = "#" }
        list.firstOrNull { it > "Z" }?.let { letters[letters.lastIndex] = "?" }
        return letters
    }

    private fun updateLetters(letters: List<String>){
        runOnMainThread {
            this.mLetters = letters
            invalidate()
        }
    }

}