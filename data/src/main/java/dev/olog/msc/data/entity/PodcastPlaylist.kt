package dev.olog.msc.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "podcast_playlist")
internal data class PodcastPlaylistEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val name: String,
        val size: Int
)

@Entity(tableName = "podcast_playlist_tracks",
        indices = [Index("playlistId")],
        foreignKeys = [
            ForeignKey(
            entity = PodcastPlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE)
        ])
internal data class PodcastPlaylistTrackEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val idInPlaylist: Long,
        val podcastId: Long,
        val playlistId: Long
)