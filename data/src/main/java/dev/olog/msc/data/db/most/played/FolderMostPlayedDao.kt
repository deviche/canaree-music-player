package dev.olog.msc.data.db.most.played

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.msc.core.entity.track.Song
import dev.olog.msc.data.entity.FolderMostPlayedEntity
import dev.olog.msc.data.entity.SongMostTimesPlayedEntity
import dev.olog.msc.shared.extensions.mapToList
import io.reactivex.Flowable
import io.reactivex.Observable

@Dao
internal abstract class FolderMostPlayedDao {

    @Query("""
        SELECT songId, count(*) as timesPlayed
        FROM most_played_folder
        WHERE folderPath = :folderPath
        GROUP BY songId
        HAVING count(*) >= 5
        ORDER BY timesPlayed DESC
        LIMIT 10
    """)
    internal abstract fun query(folderPath: String): Flowable<List<SongMostTimesPlayedEntity>>

    @Insert
    internal abstract fun insertOne(item: FolderMostPlayedEntity)

    internal fun getAll(folderPath: String, songList: Observable<List<Song>>): Observable<List<Song>> {
        return this.query(folderPath)
                .toObservable()
                .switchMap { mostPlayedSongs -> songList.map { songList ->
                    mostPlayedSongs.mapNotNull { mostPlayed ->
                        val song = songList.firstOrNull { it.id == mostPlayed.songId }
                        if (song != null) song to mostPlayed.timesPlayed
                        else null
                    }.sortedWith(compareByDescending { it.second })
                    }.mapToList { it.first }
                }
    }

}
