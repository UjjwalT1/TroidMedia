import Functions.SavedMedia
import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import ViewModel.TroidViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import Sections.Base
import Theme.Colors.clrTheme
import Videoplayer.MediaPlayer
import Videoplayer.OnGoingVideo
import ViewModel.HistoryAccess
import ViewModel.PathAccess
import ViewModel.PlayerSettingAccess
import androidx.compose.material.Colors
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.sun.jna.NativeLibrary
import dataClasses.Node
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.caprica.vlcj.binding.lib.LibVlc
import uk.co.caprica.vlcj.binding.support.runtime.RuntimeUtil

@Composable
@Preview
fun App(viewModel: TroidViewModel ) {
//    val scope = rememberCoroutineScope()
//    val tree = viewModel.tree
//    scope.launch(Dispatchers.IO) {
//        SavedMedia(viewModel , tree)
//    }
    MaterialTheme(colors = clrTheme) {
        Box(Modifier.fillMaxSize()){
            Base(viewModel)
            if(viewModel.pathIfVideoClicked.value != null)OnGoingVideo(viewModel)
        }
    }
}

fun main() = application {

    Window( state = WindowState(/*width = 1280.dp , height = 720.dp*/ position = WindowPosition(Alignment.Center) , placement = WindowPlacement.Maximized),onCloseRequest = ::exitApplication , title = "   Troid Player" , icon = painterResource("icons/icon.png") ) {
        App(TroidViewModel(HistoryAccess() , PathAccess() , PlayerSettingAccess()))
    }
}
