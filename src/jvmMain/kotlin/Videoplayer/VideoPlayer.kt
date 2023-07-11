package Videoplayer


import Functions.WriteText
import Theme.Colors.grey
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ImageInfo
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat
import java.nio.ByteBuffer


@Composable
fun VideoPlayer(
    mrl: String,
//    videoInfo: VideoInfo,
    state: VideoPlayerState,
    modifier: Modifier = Modifier,
) {
    val exifRotation = remember(mrl) {
        checkRotation(mrl)
    }

    var imageBitmap by remember(mrl) { mutableStateOf<ImageBitmap?>(null) }
    var mediaPlayerRead by remember(mrl) { mutableStateOf(false) }


    if(mediaPlayerRead) {
        imageBitmap?.let {
            Image(it, null , modifier)
        } ?: run {
            Box(modifier = modifier.background(grey))
        }
    } else {
        Box(modifier = modifier.background(Color.Black))
    }

    val mediaPlayer = remember(mrl) {
        var byteArray :ByteArray? = null
        var info: ImageInfo? = null
        val factory = MediaPlayerFactory()
        val embeddedMediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer()
        val callbackVideoSurface = CallbackVideoSurface(
            object : BufferFormatCallback {
                override fun getBufferFormat(sourceWidth: Int, sourceHeight: Int): BufferFormat {

                    info = ImageInfo.makeN32(sourceWidth, sourceHeight, ColorAlphaType.OPAQUE)
                    return RV32BufferFormat(sourceWidth, sourceHeight)
                }

                override fun allocatedBuffers(buffers: Array<out ByteBuffer>) {
                    WriteText("ByteArrAllocation ")
                    byteArray =  ByteArray(buffers[0].limit())

                }
            },
            object : RenderCallback {
                var pos: Float = -1f

                override fun display(
                    mediaPlayer: MediaPlayer,
                    nativeBuffers: Array<out ByteBuffer>,
                    bufferFormat: BufferFormat?
                ) {
                    if(!mediaPlayer.status().isPlaying && pos == mediaPlayer.status().position()) {
                        return
                    }
                    pos = mediaPlayer.status().position()

                    val byteBuffer = nativeBuffers[0]

                    byteBuffer.get(byteArray)
                    byteBuffer.rewind()
                    val bmp = Bitmap()
                    bmp.allocPixels(info!!)
                    bmp.installPixels(byteArray)
                    imageBitmap = bmp.asComposeImageBitmap()
                }
            },
            true,
            VideoSurfaceAdapters.getVideoSurfaceAdapter(),
        )
        embeddedMediaPlayer.videoSurface().set(callbackVideoSurface)
        embeddedMediaPlayer
    }


    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = mrl) {
        //println("Launched effect")
        mediaPlayer.audio()//.mute()

        if(exifRotation != 0) {
            removeRotation(mrl)
        }

        mediaPlayer.media().play(mrl)

        mediaPlayer.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {

            override fun timeChanged(mediaPlayer: MediaPlayer?, newTime: Long) {
                super.timeChanged(mediaPlayer, newTime)
            }

            override fun mediaPlayerReady(mediaPlayer: MediaPlayer) {
                super.mediaPlayerReady(mediaPlayer)

               // println("mediaPlayerReady ${mediaPlayer.video().videoDimension().width} ${mediaPlayer.video().videoDimension().height}")

                mediaPlayer.submit {
                    if(mediaPlayer.audio().isMute)
                    mediaPlayer.audio().mute()
                    //mediaPlayer.controls().setTime(1L)
                    mediaPlayer.controls()//.pause()
                    coroutineScope.launch {
                        delay(100)
                        mediaPlayerRead = true
                        state.onMediaPlayerReady(mediaPlayer)
                    }
                }

                if(exifRotation != 0) {
                    setRotation(mrl, exifRotation)
                }
            }
        })
    }

    DisposableEffect(key1 = mrl, effect = {
        this.onDispose {
            mediaPlayer.release()
        }
    })
}

fun checkRotation(
    videoFilePath: String,
): Int {
    return 0

}

private fun removeRotation(videoFilePath: String) {
    setRotation(videoFilePath, 0)
}

private fun setRotation(
    videoFilePath: String,
    rotation: Int,
) {

//    val exiftool = AdditionalLibrariesUtil.getExiftoolPath()
//    return Runtime.getRuntime().exec(
//        arrayOf(
//            exiftool,
//            "-api", "LargeFileSupport=1",
//            "-rotation=$rotation",
//            videoFilePath,
//        )
//    ).let {
//        Reader.slurp(it.inputStream, 1000)
//    }
}