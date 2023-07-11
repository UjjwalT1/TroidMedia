package Theme

import ViewModel.TroidViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.NativeLibrary
import kotlinx.coroutines.flow.MutableStateFlow
import uk.co.caprica.vlcj.binding.lib.LibVlc
import uk.co.caprica.vlcj.binding.support.runtime.RuntimeUtil
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.player.base.LibVlcConst
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.math.max
import kotlin.math.min


fun Temp(viewModel: TroidViewModel){

        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcCoreLibraryName(),"C:\\Program Files\\VideoLAN\\VLC")
        val mediaPlayerComponent = EmbeddedMediaPlayerComponent()

        val a = JFrame()
        a.contentPane = mediaPlayerComponent
        a.bounds = Rectangle(200,200,800,600)
        a.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                mediaPlayerComponent.release()
                println("Window is closing")
            }
        })
        a.isVisible = true
        mediaPlayerComponent.mediaPlayer().media().play(viewModel.pathIfVideoClicked.value!!.path)
}





