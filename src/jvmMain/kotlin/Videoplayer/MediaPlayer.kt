package Videoplayer


import Functions.saveImageToDesktop
import ViewModel.TroidViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import java.io.File

class MediaPlayer(private val mediaPlayer: MediaPlayer) {
    fun play() {
        mediaPlayer.controls().play()
    }
    fun pause() {
        mediaPlayer.controls().pause()
    }
    fun setVolume(value : Int,viewModel: TroidViewModel){
        val newValue = if(value < 0 ) 0 else if(value > 200 ) 200 else value
        viewModel.currentVolume = newValue
        mediaPlayer.audio().setVolume(newValue)
    }
    fun incVolume(value:Int,viewModel: TroidViewModel){
        val temp = getVolume()
        val newValue = if(temp+value*2 < 0 ) 0 else if(temp+value*2 > 200 ) 200 else temp+value*2
        mediaPlayer.audio().setVolume(newValue)
        viewModel.currentVolume = newValue
    }
    fun getVolume():Int{
        return mediaPlayer.audio().volume()
    }
    fun toggleMute(viewModel: TroidViewModel){
        mediaPlayer.audio().mute()
        viewModel.isMute = !mediaPlayer.audio().isMute
    }

    fun getSnap(name:String) {
        mediaPlayer.snapshots().save(File("ScreenShots/${name}.png"))
    }




    val isPlaying: Boolean
        get() = mediaPlayer.status().isPlaying

    fun setRate(rate: Float) {
        mediaPlayer.controls().setRate(rate)
    }
    fun setTime(millis: Long) {
        mediaPlayer.controls().setTime(millis)
    }

    fun setTimeAccurate(millis: Long) {
        mediaPlayer.controls().setTime(millis)
    }

    fun getTimeMillis(): Long {
        return mediaPlayer.status().time()
    }

    fun getLengthMillis(): Long {
        return mediaPlayer.status().length()
    }

    fun addOnTimeChangedListener(listener: OnTimeChangedListener) {
        mediaPlayer.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun timeChanged(mediaPlayer: MediaPlayer?, newTime: Long) {
                super.timeChanged(mediaPlayer, newTime)
                listener.onTimeChanged(newTime)
            }
        })
    }

    fun dispose() {
        mediaPlayer.release()
    }
}

interface OnTimeChangedListener {
    fun onTimeChanged(timeMillis: Long)
}

