package dataClasses

import Functions.getDate
import java.io.File

data class SideBarItems(val icon:String, val name:String, val onClick:()->Unit)

data class MediaData(val name:String , val path : String , val size:String , val mediaExtension:String , val imgPath: String = "src/jvmMain/resources/Thumbnails/${File(path).name}.png")

val extensionToType = mapOf(
    ".mp4" to "Video" ,
    ".avi" to "Video" ,
    ".mov" to "Video" ,
    ".wmv" to "Video" ,
    ".mkv" to "Video" ,
    ".MP4" to "Video" ,
    ".AVI" to "Video" ,
    ".MOV" to "Video" ,
    ".WMV" to "Video" ,
    ".MKV" to "Video" ,
    ".mp3" to "Audio" ,
    ".wav" to "Audio" ,
    ".aac" to "Audio" ,
    ".flac" to "Audio" ,
    ".ogg" to "Audio" ,
    ".MP3" to "Audio" ,
    ".WAV" to "Audio" ,
    ".AAC" to "Audio" ,
    ".FLAC" to "Audio" ,
    ".OGG" to "Audio" ,
)

/**
 * This data class is representing a single application folder and will be used to make tree
 */
data class Node(val type:String = "" ,
                val lvl:Int = 0,
                val curr:String = "" ,
                val mediaList:MutableList<MediaData>  = mutableListOf() ,
                val subDir:MutableList<Node> = mutableListOf()
)

data class HistoryData(
    var timeStamp: Long = System.currentTimeMillis(),
    val path:String = "" , val mediaLength:String = "00 hr 00 min ", val title:String = "Play a media to view here", val imgPath:String = "images/giga.jpeg",
                val percentWatched:Float = 0f , val time:String = getDate(timeStamp).first , val date:String = getDate(timeStamp).second ,
    val exists:Boolean = true
)

data class SavedPath(var uniqueID :String = System.currentTimeMillis().toString(), var path :String ="")
