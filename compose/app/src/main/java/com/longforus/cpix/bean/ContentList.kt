package com.longforus.cpix.bean

import android.os.Parcelable
import androidx.compose.ui.semantics.Role
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Transient
import io.objectbox.converter.PropertyConverter
import kotlinx.coroutines.CoroutineStart
import kotlinx.parcelize.Parcelize


data class ContentListBean(
    var hits: List<Item> = listOf(),
    var total: Int = 0, // 1433183
    var totalHits: Int = 0 // 500
)


@Parcelize
@Entity
data class Item(
    var collections: Int = 0, // 2687
    var comments: Int = 0, // 507
    var downloads: Int = 0, // 1075242
    @Id(assignable = true)
    var id: Long = 0, // 3605547
    var imageHeight: Int = 0, // 3894
    var imageSize: Int = 0, // 3590092
    var imageWidth: Int = 0, // 6000
    var largeImageURL: String = "", // https://pixabay.com/get/g36f515e410ee10b43d2da5e165a36aea216baec423d96c2369d9d64d301f3be833573acad871f2459572d8ba03a5d5d60ae62388c3d3fc8efd5ca50fba2cada4_1280.jpg
    var likes: Int = 0, // 2996
    var pageURL: String = "", // https://pixabay.com/photos/ocean-milky-way-boat-sailing-3605547/
    var previewHeight: Int = 0, // 97
    var previewURL: String = "", // https://cdn.pixabay.com/photo/2018/08/14/13/23/ocean-3605547_150.jpg
    var previewWidth: Int = 0, // 150
    var tags: String = "", // ocean, milky way, boat
    var type: String = "", // photo
    var user: String = "", // jplenio
    var user_id: Int = 0, // 7645255
    var userImageURL: String = "", // https://cdn.pixabay.com/user/2021/02/03/19-57-56-895_250x250.jpg
    var views: Int = 0, // 1790495
    var webformatHeight: Int = 0, // 415
    var webformatURL: String = "", // https://pixabay.com/get/gca2881aa72e3bdd30ba44211b8b077e778c6ea8bd1f684729db5586e9eb93dbf8b15c9d2681fe681b41e76ce11a38d451f87269433daa8f5b9d4c625eaf4466f_640.jpg
    var webformatWidth: Int = 0, // 640
    var favoriteDate: Long = 0L,
    var lastUpdateDate: Long = System.currentTimeMillis(),

    var duration: Int = 0, // 12
    var picture_id: String = "", // 529927645
    @Convert(converter = VideosConverter::class, dbType = String::class)
    var videos: Videos = Videos(),
) : Parcelable {
    val saveName: String
        get() = "${id}_${imageWidth}*${imageHeight}.jpg"
    val coverImageUrl: String
        get() {
            if (isVideo) {
                return "https://i.vimeocdn.com/video/${picture_id}_640x360.jpg"
            }
            return webformatURL
        }
    val isVideo: Boolean
        get() = type in listOf("film", "animation")
}


@Parcelize
data class Videos(
    @Convert(converter = VideoConverter::class, dbType = String::class)
    var large: Video = Video(),
    @Convert(converter = VideoConverter::class, dbType = String::class)
    var medium: Video = Video(),
    @Convert(converter = VideoConverter::class, dbType = String::class)
    var small: Video = Video(),
    @Convert(converter = VideoConverter::class, dbType = String::class)
    var tiny: Video = Video()
) : Parcelable {

}

@Parcelize
data class Video(
    var height: Int = 0, // 1080
    var size: Int = 0, // 6615235
    var url: String = "", // https://player.vimeo.com/external/135736646.hd.mp4?s=ed02d71c92dd0df7d1110045e6eb65a6&profile_id=119
    var width: Int = 0 // 1920
) : Parcelable


class VideosConverter : PropertyConverter<Videos?, String?> {

    override fun convertToEntityProperty(databaseValue: String?): Videos {
        return OB.fromJson(databaseValue ?: "", Videos::class.java)
    }

    override fun convertToDatabaseValue(entityProperty: Videos?): String {
        return entityProperty.toJson()
    }

}

class VideoConverter : PropertyConverter<Video?, String?> {

    override fun convertToEntityProperty(databaseValue: String?): Video {
        return OB.fromJson(databaseValue ?: "", Video::class.java)
    }

    override fun convertToDatabaseValue(entityProperty: Video?): String {
        return entityProperty.toJson()
    }

}