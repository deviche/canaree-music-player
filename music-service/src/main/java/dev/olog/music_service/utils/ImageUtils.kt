package dev.olog.music_service.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.provider.MediaStore

object ImageUtils {

    fun getBitmapFromUriWithPlaceholder(context: Context, uri: Uri, id: Long): Bitmap? {
        return try {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } catch (ex: Exception) {
            getPlaceholderAsBitmap(context, id)
        }
    }

    private fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun getPlaceholderAsBitmap(context: Context, id: Long): Bitmap {
        val size = context.dip(128)
        val drawable = CoverUtils.getGradient(context, id)

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, size, size)
        drawable.draw(canvas)

        return bitmap
    }

    fun getBitmapFromUri(context: Context, coverUri: String?): Bitmap? {
        if (coverUri == null){
            return null
        }

        return try {
            MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(coverUri))
        } catch (ex: Exception) {
            null
        }
    }

}