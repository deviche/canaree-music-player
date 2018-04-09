package dev.olog.msc.data.mapper

import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.core.database.getLong
import androidx.core.database.getString
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.utils.img.ImagesFolderUtils
import java.io.File

fun Cursor.toFakeSong(): Song {
    val id = getLong(BaseColumns._ID)
    val artistId = getLong(MediaStore.Audio.AudioColumns.ARTIST_ID)
    val albumId = getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)

    val duration = getLong(MediaStore.Audio.AudioColumns.DURATION)
    val dateAdded = getLong(MediaStore.MediaColumns.DATE_ADDED)

    val path = getString(MediaStore.MediaColumns.DATA)
    val folder = extractFolder(path)

    return Song(
            id, artistId, albumId,
            "An awesome song",
            "An awesome artist",
            "An awesome album",
            ImagesFolderUtils.forAlbum(albumId),
            duration, dateAdded, false, false,
            path, folder, -1, -1
    )
}

private fun extractFolder(path: String): String {
    val lastSep = path.lastIndexOf(File.separator)
    val prevSep = path.lastIndexOf(File.separator, lastSep - 1)
    return path.substring(prevSep + 1, lastSep)
}