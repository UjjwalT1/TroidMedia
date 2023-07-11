package Videoplayer

import Functions.tsToTime
import Theme.Colors.grey
import Theme.Colors.red
import Theme.Fonts.Hind
import Theme.Fonts.MonomaniacOne
import Theme.Temp
import ViewModel.TroidViewModel
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import javax.sound.sampled.*
import javax.sound.sampled.Control.Type
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round


@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun OnGoingVideo(viewModel: TroidViewModel) {
    var currentVolume = viewModel.currentVolume

    val playerConfig  = viewModel.playerConfiguration.value
    var clr by remember { mutableStateOf(false) }
    val color = animateColorAsState( if(clr)red.copy(.6f) else red.copy(.2f))
    val overlay = viewModel.overlay
    val videoFile = viewModel.pathIfVideoClicked.value!!.path
    val netDiff = viewModel.netDiff

    val videoPlayerState =  remember { VideoPlayerState() }

    var isPlaying by remember(videoFile) { mutableStateOf(true) }
    val timeMillisStateFlow = remember(videoFile) { MutableStateFlow(-1L) }
    val lengthMillisStateFlow = remember(videoFile) { MutableStateFlow(-1L) }

    val timeMillis by timeMillisStateFlow.collectAsState()
    val lengthMillis by lengthMillisStateFlow.collectAsState()
    val requester = remember { FocusRequester() }

    // TODO : MainBox Holding video and overlay
   Box(Modifier.fillMaxSize().background(Color.Black).pointerInput(Unit){
           detectTapGestures(onTap = {}) //For Preventing click to pass to children
       }
       .onKeyEvent {
           viewModel.job?.cancel()
           viewModel.setOverlay()
           //TODO : -10 secs
           if( it.key == Key.DirectionLeft && it.type == KeyEventType.KeyDown){
               videoPlayerState.doWithMediaPlayer { mediaPlayer ->
                   mediaPlayer.setTimeAccurate( max(mediaPlayer.getTimeMillis() - playerConfig.forwardLength, 0 ) )
               }
           }
           //TODO : +10 secs
           else if(it.key == Key.DirectionRight && it.type == KeyEventType.KeyDown){
               videoPlayerState.doWithMediaPlayer { mediaPlayer ->
                   mediaPlayer.setTimeAccurate( min(mediaPlayer.getTimeMillis() + playerConfig.forwardLength, mediaPlayer.getLengthMillis()) )
               }
           }
           //TODO : Pause/Play
           else if(it.key == Key.Spacebar && it.type == KeyEventType.KeyDown){
               videoPlayerState.doWithMediaPlayer { mediaPlayer ->
                   if(mediaPlayer.isPlaying) {
                       isPlaying = false
                       mediaPlayer.pause()
                   } else {
                       mediaPlayer.play()
                       isPlaying = true
                   }
               }
           }
           //TODO : Volume Up 3 Unit
           else if(it.key == Key.DirectionUp && it.type == KeyEventType.KeyDown){
               viewModel.seekSet("Volume")
               viewModel.seekJob?.cancel()
               videoPlayerState.doWithMediaPlayer { mediaPlayer ->
                   mediaPlayer.incVolume(3,viewModel)
               }
           }
           //TODO : Volume Up -3 Unit
           else if(it.key == Key.DirectionDown && it.type == KeyEventType.KeyDown){
               viewModel.seekJob?.cancel()
               viewModel.seekSet("Volume")
               videoPlayerState.doWithMediaPlayer { mediaPlayer ->
                   mediaPlayer.incVolume(-3,viewModel)
               }
           }

           true
       }
       .onPointerEvent(PointerEventType.Scroll){
           viewModel.seekJob?.cancel()
           viewModel.seekSet("Volume")
           if(it.changes.last().scrollDelta.y.toInt() >= 1){
               videoPlayerState.doWithMediaPlayer { mediaPlayer ->
                   mediaPlayer.incVolume(-playerConfig.volumeChangeOnScroll,viewModel)
               }
           }else{
               videoPlayerState.doWithMediaPlayer { mediaPlayer ->
                   mediaPlayer.incVolume(playerConfig.volumeChangeOnScroll,viewModel)
               }
           }
       }.focusRequester(requester) .focusable()
   ){
       if(lengthMillis>0 && try {
               viewModel.latestForGigaTile!!.path == viewModel.latestForGigaTileBackUp!!.path
           }catch (_:Exception){false})
       LaunchedEffect(lengthMillis){
           withContext(Dispatchers.IO){
               videoPlayerState.doWithMediaPlayer { mediaPlayer ->
                  mediaPlayer.setTime((viewModel.latestForGigaTileBackUp!!.percentWatched*lengthMillis).toLong())
               }
           }
       }

        //TODO                  VIDEO INITIALISATION
       LaunchedEffect(videoFile) {
           viewModel.netDiff = 0
           videoPlayerState.doWithMediaPlayer { mediaPlayer ->
               lengthMillisStateFlow.value = mediaPlayer.getLengthMillis()
               mediaPlayer.addOnTimeChangedListener(object : OnTimeChangedListener{
                   override fun onTimeChanged(timeMillis: Long) {
                       timeMillisStateFlow.value = timeMillis
                   }
               })
           }
           videoPlayerState.doWithMediaPlayer { mediaPlayer ->
               viewModel.currentVolume = mediaPlayer.getVolume()
           }


       }

        //TODO                Code to update history every 10 seconds
       if(  abs((timeMillis/1000) - netDiff ) > 10){
           viewModel.netDiff += ((timeMillis.floorDiv(1000)) - netDiff).toInt() -1
           viewModel.updateHistory(timeMillis.toFloat()/lengthMillis.toFloat())
       }

        //TODO:                      VIDEO COMPOSABLE
       Column(Modifier.fillMaxSize().onClick {  if(!overlay)viewModel.setOverlay() else viewModel.resetOverlay() }){
           var change = viewModel.seek
           Box(Modifier.fillMaxSize().focusRequester(requester) .focusable()){
               VideoPlayer( mrl = videoFile, state = videoPlayerState , Modifier.fillMaxSize().focusRequester(requester) .focusable())
               Row(Modifier.fillMaxSize()){
                   Column(Modifier.fillMaxHeight().weight(1f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                       AnimatedVisibility(change =="LeftSpecialSeek", exit = scaleOut()) {
                           Text(
                               "-${playerConfig.seekSeconds / 1000}",
                               fontFamily = MonomaniacOne,
                               color = Color.White,
                               fontSize = 60.sp
                           )
                       }
                       AnimatedVisibility(change =="LeftBtnSeekSeek", exit = scaleOut()) {
                           Text(
                               "-${playerConfig.seekSeconds / 1000}",
                               fontFamily = MonomaniacOne,
                               color = Color.White,
                               fontSize = 60.sp
                           )
                       }
                   }
                   Spacer(Modifier.weight(1f))
                   Column(Modifier.fillMaxHeight().weight(1f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                       AnimatedVisibility(change =="RightSpecialSeek", exit = scaleOut()) {
                           Text(
                               "+${playerConfig.seekSeconds/1000}",
                               fontFamily = MonomaniacOne,
                               color = Color.White,
                               fontSize = 60.sp
                           )
                       }
                       AnimatedVisibility(change =="RightBtnSeek", exit = scaleOut()) {
                           Text(
                               "-${playerConfig.seekSeconds / 1000}",
                               fontFamily = MonomaniacOne,
                               color = Color.White,
                               fontSize = 60.sp
                           )
                       }
                   }
               }
               Row(Modifier.fillMaxSize()){
                   Column(Modifier.fillMaxHeight().weight(1f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                   }
                   Spacer(Modifier.weight(1f))
                   Column(Modifier.fillMaxHeight().weight(1f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.End){
                       AnimatedVisibility(change =="Volume", exit = scaleOut()) {
                           Column(horizontalAlignment = Alignment.CenterHorizontally ){
                               Text(
                                   "${((currentVolume.toFloat()/200)*100).toInt()}%",
                                   fontFamily = MonomaniacOne,
                                   color =red,
                                   fontSize = 20.sp
                               )
                               Spacer(Modifier.height(7.dp))
                               Box(Modifier.width(20.dp).height(400.dp)){
                                   Spacer(Modifier.clip(RoundedCornerShape(50.dp)).fillMaxSize().background(grey))
                                   Spacer(Modifier.align(Alignment.BottomCenter).clip(RoundedCornerShape(50.dp)).fillMaxWidth().fillMaxHeight(currentVolume.toFloat()/200).background(red))
                               }
                           }


                       }
                   }
                   Spacer(Modifier.width(40.dp))
               }
           }

       }


       AnimatedVisibility(overlay ,enter = fadeIn() , exit = fadeOut()){
           Column(Modifier.fillMaxSize().background( Brush.verticalGradient( listOf( /*Color.Black,*/Color.Black.copy(.3f),Color.Black ) ) )
               .focusRequester(requester) .focusable()
           ){
               Spacer(Modifier.height(0.dp))
               //TODO:               TOP BAR OF VIDEO
               Column(Modifier.fillMaxWidth().background(Color.Black.copy(.7f)) ){
                   Spacer(Modifier.height(10.dp))
                   Row(verticalAlignment = Alignment.CenterVertically) {
                       Spacer(Modifier.width(15.dp))
                       IconButton(onClick = {viewModel.pathIfVideoClicked.value = null; viewModel.refreshHistory()},Modifier.size(40.dp).clip(
                           RoundedCornerShape(50.dp)
                       ).background(color.value).onPointerEvent(
                           PointerEventType.Enter){clr = true}.onPointerEvent(PointerEventType.Exit){clr = false}){
                           Image(painterResource("icons/x-mark3.png"),contentDescription = null, Modifier.size(20.dp) )
                       }
                       Spacer(Modifier.width(45.dp))
                       Text(File(viewModel.pathIfVideoClicked.value?.path?:"Blank").name, maxLines = 1, overflow = TextOverflow.Ellipsis, fontFamily = MonomaniacOne  , color = red, fontSize = 25.sp , letterSpacing = 1.sp)
                       Spacer(Modifier.weight(1f))
                   }

               }
               Spacer(Modifier.weight(1f))

               //TODO :             PROGRESS BAR
               Row(Modifier.fillMaxWidth().padding(10.dp,0.dp)){
                   if(lengthMillis != -1L) {
                       Slider(
                           value = timeMillis/lengthMillis.toFloat(),
                           onValueChange = {
                               viewModel.job?.cancel()
                               viewModel.setOverlay()
                               videoPlayerState.doWithMediaPlayer {mediaPlayer ->
                                   timeMillisStateFlow.value = (it*lengthMillis).toLong()
                                   mediaPlayer.setTime((it*lengthMillis).toLong())
                               }
                           },
                           modifier= Modifier.fillMaxWidth().height(5.dp),
                           colors = SliderDefaults.colors(inactiveTrackColor = grey , activeTrackColor = red , thumbColor = Color.Transparent)
                       )
                   }
               }
               //TODO :             TIMER
               Row(Modifier.fillMaxWidth().padding(15.dp,3.dp,10.dp,0.dp)){
                   Text(tsToTime(timeMillis), fontFamily = Hind  , color = Color.White , fontSize = 16.sp)
                   Spacer(Modifier.weight(1f))
                   val temp = remember { mutableStateOf(false) }
                   Text("${if(temp.value)"-" else ""} ${tsToTime(if(temp.value)lengthMillis-timeMillis else lengthMillis)}", fontFamily = Hind  , color = Color.White , fontSize = 16.sp ,modifier= Modifier.clickable {
                       temp.value = !temp.value
                   })
               }
               //TODO :             CONTROLS
               Box(Modifier.fillMaxWidth().padding(20.dp,0.dp)){
                   Row(Modifier.align(Alignment.CenterStart)){ ScreenShot(videoPlayerState, viewModel) }
                   Row(Modifier.align(Alignment.Center),verticalAlignment = Alignment.CenterVertically){
                       var clrL by remember { mutableStateOf(false) }
                       val colorL = animateColorAsState(if(clrL)Color.White.copy(.4f) else Color.White.copy(.1f))
                       var clrM by remember { mutableStateOf(false) }
                       val colorM = animateColorAsState(if(clrM)Color.White.copy(.4f) else Color.White.copy(.1f))
                       var clrR by remember { mutableStateOf(false) }
                       val colorR = animateColorAsState(if(clrR)Color.White.copy(.4f) else Color.White.copy(.1f))
                       Spacer(Modifier.weight(1f))
                       //TODO;      SEEK BACK BUTTON
                       IconButton(onClick = {
                           viewModel.job!!.cancel()
                           viewModel.setOverlay()
                           viewModel.seekSet("LeftSpecialSeek")
                           videoPlayerState.doWithMediaPlayer { mediaPlayer ->
                               mediaPlayer.setTimeAccurate( max(mediaPlayer.getTimeMillis() - playerConfig.seekSeconds, 0) )
                           }
                       } , Modifier.size(35.dp).clip(RoundedCornerShape(50.dp)).background(colorL.value)
                           .onPointerEvent(PointerEventType.Enter){clrL = true}.onPointerEvent(PointerEventType.Exit){clrL = false}
                       ){
                           Box(){
                               Image(painterResource("icons/minus3.png"),contentDescription = null, Modifier.size(32.dp).align(
                                   Alignment.Center))
                               Text("${playerConfig.seekSeconds/1000}", fontSize = 14.sp, fontFamily = MonomaniacOne,modifier = Modifier.align(
                                   Alignment.Center), color = Color.White)
                           }


                       }
                       Spacer(Modifier.width(30.dp))

                       //TODO;      PLAY PAUSE BUTTON
                       IconButton(onClick = {
                           viewModel.job!!.cancel()
                           viewModel.setOverlay()
                           videoPlayerState.doWithMediaPlayer { mediaPlayer ->
                              // mediaPlayer.getSnap(File(viewModel.pathIfVideoClicked.value!!.path).name)
                               if(mediaPlayer.isPlaying) {
                                   isPlaying = false
                                   mediaPlayer.pause()
                               } else {
                                   mediaPlayer.play()
                                   isPlaying = true
                               }
                           }
                       } , Modifier.size(55.dp).clip(RoundedCornerShape(50.dp)).background(colorM.value)
                           .onPointerEvent(PointerEventType.Enter){clrM = true}.onPointerEvent(PointerEventType.Exit){clrM = false}
                       ){
                           AnimatedContent(isPlaying){
                               if(it)Image(painterResource("icons/pause2.png"),contentDescription = null, Modifier.size(30.dp) )
                               else Image(painterResource("icons/play2.png"),contentDescription = null, Modifier.size(30.dp) )
                           }

                       }
                       Spacer(Modifier.width(30.dp))

                       //TODO;      SEEK FWD BUTTON
                       IconButton(onClick = {
                           viewModel.job!!.cancel()
                           viewModel.setOverlay()
                           viewModel.seekSet("RightSpecialSeek")
                           videoPlayerState.doWithMediaPlayer { mediaPlayer ->
                               mediaPlayer.setTimeAccurate( min(mediaPlayer.getTimeMillis() + playerConfig.seekSeconds, mediaPlayer.getLengthMillis()) )
                           }
                       } , Modifier.size(35.dp).clip(RoundedCornerShape(50.dp)).background(colorR.value)
                           .onPointerEvent(PointerEventType.Enter){clrR = true}.onPointerEvent(PointerEventType.Exit){clrR = false}
                       ){
                           Box(){
                               Image(painterResource("icons/plus3.png"),contentDescription = null, Modifier.size(32.dp).align(
                                   Alignment.Center))
                               Text("${playerConfig.seekSeconds/1000}", fontSize = 14.sp, fontFamily = MonomaniacOne,modifier = Modifier.align(
                                   Alignment.Center), color = Color.White)
                           }
                       }
                       Spacer(Modifier.weight(1f))

                   }
                   Row(Modifier.align(Alignment.CenterEnd)){
                       ControlPanel(videoPlayerState , viewModel , currentVolume)
                   }
               }

               Spacer(Modifier.height(30.dp))

           }

       }


   }
    LaunchedEffect(Unit){
        requester.requestFocus()
    }

}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun ControlPanel(videoPlayerState :  VideoPlayerState , viewModel: TroidViewModel , vol:Int){
    Row(Modifier.width(220.dp),verticalAlignment = Alignment.CenterVertically) {
       var volHover by remember { mutableStateOf(false) }
       val colorVol = animateColorAsState(if(volHover)red.copy(.3f) else Color.Transparent)
       IconButton(onClick = {
           videoPlayerState.doWithMediaPlayer { mediaPlayer ->
               mediaPlayer.toggleMute(viewModel)
           }
       } , Modifier.clip(RoundedCornerShape(50.dp)).background(colorVol.value).size(40.dp)
           .onPointerEvent(PointerEventType.Enter){volHover = true}.onPointerEvent(PointerEventType.Exit){volHover = false}){
           AnimatedContent(viewModel.isMute){
               if(it) Image(painterResource("icons/mute2.png"),contentDescription = null, Modifier.size(25.dp))
               else Image(painterResource("icons/volume2.png"),contentDescription = null, Modifier.size(25.dp))
           }

       }

        Spacer(Modifier.width(10.dp))
       Column {
           Row {
               Spacer(Modifier.weight(1f))
               Text("${((vol.toFloat()/200)*100).toInt()}%",color = Color.White , fontFamily = MonomaniacOne , fontSize = 16.sp)
               Spacer(Modifier.width(20.dp))
           }
           Spacer(Modifier.height(4.dp))
           Slider(
               value = (vol.toFloat()/200),
               onValueChange = {
                   viewModel.job?.cancel()
                   viewModel.setOverlay()
                   videoPlayerState.doWithMediaPlayer {mediaPlayer ->
                       mediaPlayer.setVolume((it*100).toInt()*2, viewModel )
                   }
               },
               modifier= Modifier.width(160.dp).height(3.dp),
               colors = SliderDefaults.colors(inactiveTrackColor = grey , activeTrackColor = red , thumbColor = Color.Transparent)
           )

       }
        Spacer(Modifier.width(60.dp))
   }

}



@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun ScreenShot(videoPlayerState :  VideoPlayerState , viewModel: TroidViewModel){
    var volHover by remember { mutableStateOf(false) }
    val colorVol = animateColorAsState(if(volHover)red.copy(.3f) else Color.Transparent)
    Spacer(Modifier.width(20.dp))
    IconButton(onClick = {
        videoPlayerState.doWithMediaPlayer { mediaPlayer ->
            mediaPlayer.getSnap(File(viewModel.pathIfVideoClicked.value!!.path).name)
        }
    } , Modifier.clip(RoundedCornerShape(50.dp)).background(colorVol.value).size(40.dp)
        .onPointerEvent(PointerEventType.Enter){volHover = true}.onPointerEvent(PointerEventType.Exit){volHover = false}){
        Image(painterResource("icons/snap.png"),contentDescription = null, Modifier.size(25.dp))

    }
}

