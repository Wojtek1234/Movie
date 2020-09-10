package pl.wojtek.core.common

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *
 */

@Parcelize
data class ReturnData<T : Parcelable>(val reload: Boolean, val data: T?) : Parcelable {
    private var consumed = false

    fun consume(block: ReturnData<T>.(T?) -> Unit) {
        if (!consumed) {
            consumed = true
            block(data)
        }
    }
}


@Parcelize
data class ReturnReloadData(val reload: Boolean) : Parcelable {
    private var consumed = false

    fun consume(block: ReturnReloadData.() -> Unit) {
        if (!consumed) {
            consumed = true
            block()
        }
    }
}


const val quiz_return_value = "reload_quiz_data"
const val chapter_return_value = "reload_chapter_data"
const val subject_return_value = "reload_subject_data"

@Parcelize
data class QuizInfo(val quizId: String) : Parcelable

@Parcelize
data class ChapterInfo(val chapterId: String) : Parcelable


@Parcelize
data class SubjectInfo(val subjectId: String) : Parcelable


@Parcelize
data class ImageData(val image: Uri) : Parcelable


@Parcelize
data class YoutubeListData(val youtubes: List<YoutubeData>,
                           val lectureName: String, val lectureId: String) : Parcelable

@Parcelize
data class YoutubeData(val youtubeVideoId: String,
                       val videoName: String,
                       val szkolniakId: String = "",
                       val duration: String = "",
                       val description: String = "",
                       val thumbnailUrl: String? = null) : Parcelable
