package Sections

import Functions.log
import Theme.Colors.grey
import Theme.Colors.red
import Theme.Fonts.MonomaniacOne
import ViewModel.TroidViewModel
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import Theme.Colors.clrTheme
import Theme.Colors.darkGrey
import Theme.Fonts.Hind
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.*
import dataClasses.HistoryData
import dataClasses.extensionToType
import java.io.File


@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun Home(viewModel: TroidViewModel){
    var expanded by remember { mutableStateOf(true) }
    var number by remember { mutableStateOf(0f) }
    val lazyGridState = rememberLazyGridState()
    val isAtEnd = lazyGridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == lazyGridState.layoutInfo.totalItemsCount - 1
    val latestForGigaTile = viewModel.latestForGigaTile?: HistoryData()
    val homeDir = viewModel.pathIndex[File(latestForGigaTile.path).parent]?.toList()

    Column(
        Modifier.fillMaxSize().padding(20.dp,2.dp) .onPointerEvent(PointerEventType.Scroll){
            if(it.changes.first().scrollDelta.y == -1f && number >= 0)
            number += it.changes.first().scrollDelta.y else if(it.changes.first().scrollDelta.y != -1f && !isAtEnd) number += it.changes.first().scrollDelta.y
            expanded = (number <= 0)
        }
        , horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(Modifier.height(30.dp))
        AnimatedContent(expanded , Modifier.onPointerEvent(PointerEventType.Enter){}.onPointerEvent(PointerEventType.Exit){}
            , transitionSpec = {  fadeIn() with fadeOut()
        }){
            if(it) GigaTile({ viewModel.addHistory(
                HistoryData(path = latestForGigaTile.path , mediaLength = latestForGigaTile.mediaLength , title = latestForGigaTile.title , imgPath = latestForGigaTile.imgPath)) }
                , latestForGigaTile)
            else GigaTileCollapsed({ viewModel.addHistory(
                HistoryData(path = latestForGigaTile.path , mediaLength = latestForGigaTile.mediaLength , title = latestForGigaTile.title , imgPath = latestForGigaTile.imgPath)) }
                , latestForGigaTile)
        }
        LazyVerticalGrid(columns = GridCells.Adaptive(350.dp) , modifier = Modifier.height(900.dp), contentPadding = PaddingValues(60.dp,4.dp) ,
            state = lazyGridState){

            items(homeDir?: mutableListOf()){
                Tiles(viewModel,"23 min | ${it.size}" , it.name,if(extensionToType[it.mediaExtension]=="Video")"images/video.png" else "images/audio.png" , path = it.path
                ) {
                    println(it.mediaExtension)
                    viewModel.addHistory(
                        HistoryData(path = it.path, mediaLength = "XX hr XX min",title = it.name )
                    )
                }
            }

        }
    }
}

@Composable
fun GigaTile(onClick: () -> Unit , data:HistoryData){
    LazyColumn {
        item {
            Box(
                Modifier
                .padding(30.dp,20.dp,30.dp,7.dp)
                .shadow(Color.Black.copy(.6f),3.dp,3.dp , 12.dp,40.dp,40.dp)
                .clip(RoundedCornerShape(20.dp)).animateContentSize()

            ){
                Image(
                    painterResource(data.imgPath),contentDescription = null,
                    Modifier.fillMaxWidth(1f).clip(RoundedCornerShape(20.dp)).height(400.dp).clickable{
                        onClick()
                    },
                    contentScale = ContentScale.Crop
                )
                Row(
                    Modifier.fillMaxHeight().height(400.dp).clip(RoundedCornerShape(20.dp))
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(.7f), Color.Black))) ,
                    verticalAlignment = Alignment.Bottom
                ){
                    Column (Modifier.padding(50.dp,30.dp)){
                        Text(data.title , fontSize = 34.sp , fontFamily = MonomaniacOne, color = Color.White , maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth(.8f))
                        Text("${data.mediaLength}  | ${(data.percentWatched*100).toInt()}% Watched" , fontSize = 20.sp , fontFamily = MonomaniacOne, color = Color.White.copy(.7f))
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = {}, Modifier.padding(50.dp,50.dp)){
                        Image(painterResource("icons/play-button2.png"),contentDescription = null , Modifier.size(50.dp))
                    }
                }
                Spacer(Modifier.fillMaxWidth(data.percentWatched).background(red).height(4.dp).align(Alignment.BottomStart))

            } }
    }
}

@Composable
fun GigaTileCollapsed(onClick: () -> Unit , data:HistoryData){
    Box(
        Modifier.height(160.dp)
        .padding(30.dp,20.dp,30.dp,7.dp)
        .shadow(Color.Black.copy(.6f),3.dp,3.dp , 12.dp,40.dp,40.dp)
        .clip(RoundedCornerShape(20.dp))

    ){
        Image(
            painterResource(data.imgPath),contentDescription = null,
            Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)).clickable{
                onClick()
            },
            contentScale = ContentScale.Crop
        )
        Row(
            Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(.8f)) ,
            verticalAlignment = Alignment.CenterVertically
        ){
            Spacer(Modifier.width(40.dp))
            Column (Modifier){
                Text(data.title , fontSize = 34.sp , fontFamily = MonomaniacOne, color = Color.White , maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth(.8f))
                Text("${data.mediaLength}  | ${(data.percentWatched*100).toInt()}% Watched" , fontSize = 20.sp , fontFamily = MonomaniacOne, color = Color.White.copy(.7f))
                Spacer(Modifier.height(10.dp))
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {}, Modifier.padding(50.dp,0.dp)){
                Image(painterResource("icons/play-button2.png"),contentDescription = null , Modifier.size(50.dp))
            }
        }
        Spacer(Modifier.fillMaxWidth(data.percentWatched).background(red).height(4.dp).align(Alignment.BottomStart))

    }
}





@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Tiles(viewModel: TroidViewModel, watchTime :String, title:String, img:String ,path:String="", onClick:()->Unit = {}){
    var overlay by remember { mutableStateOf(false) }
    var drop by remember { mutableStateOf(false) }
    Column(
        Modifier.padding(10.dp).shadow(Color.Black.copy(.6f),3.dp,3.dp , 12.dp,20.dp,20.dp).width(400.dp)
        .clip(RoundedCornerShape(14.dp)).background(if(viewModel.latestForGigaTile?.path == path)red.copy(.4f)else grey), horizontalAlignment = Alignment.CenterHorizontally
    ){

        Box(Modifier.onPointerEvent(PointerEventType.Enter){ overlay = true }.onPointerEvent(PointerEventType.Exit){ overlay = false}

        ){
            Column(Modifier.fillMaxWidth(.9f).height(200.dp).padding(0.dp,10.dp,0.dp,0.dp).clip(RoundedCornerShape(14.dp))
                .background(darkGrey) , verticalArrangement = Arrangement.Center , horizontalAlignment = Alignment.CenterHorizontally){

            }

            Image(
                painterResource(img),contentDescription = null , contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth(.9f).height(200.dp).padding(0.dp,10.dp,0.dp,0.dp).clip(RoundedCornerShape(14.dp)).clickable { onClick() }
            )
            Column {
                AnimatedVisibility(overlay , enter =  fadeIn() , exit = fadeOut() ){
                    Column(Modifier.fillMaxWidth(.9f).height(200.dp).padding(0.dp,10.dp,0.dp,0.dp).clip(RoundedCornerShape(14.dp))
                        .background(Color.Black.copy(.6f)) , verticalArrangement = Arrangement.Center , horizontalAlignment = Alignment.CenterHorizontally){
                        Image(painterResource("icons/play-button2.png"),contentDescription = null , Modifier.size(50.dp))
                    }

                }
            }
        }



        Row(Modifier, verticalAlignment = Alignment.CenterVertically ){
            Column (Modifier.padding(30.dp,4.dp,0.dp ,0.dp).fillMaxWidth(.8f)){
                Text(title , fontFamily = Hind, fontSize = 21.sp , color = Color.White , overflow = TextOverflow.Ellipsis , maxLines = 1 )
                Text(watchTime , fontFamily = MonomaniacOne, fontSize = 18.sp , color =  Color.White.copy(.4f) , letterSpacing = 1.sp)
            }
            Spacer(Modifier.weight(1f))

            Box(){
                IconButton(onClick = {drop = true}, Modifier.padding(20.dp,0.dp).size(50.dp)){
                    Image(painterResource("icons/ellipsis2.png"),contentDescription = null , Modifier.size(20.dp))
                }
                MaterialTheme(shapes = Shapes(medium = RoundedCornerShape(12.dp) , large = RoundedCornerShape(20.dp) , small = RoundedCornerShape(7.dp) ),
                    colors = clrTheme
                    ){
                    DropdownMenu(drop , {drop = false} , modifier = Modifier.width(150.dp)){
                        Item("DELETE","icons/trash.png",{})
                        Item("STREAM","icons/radio2.png",{})
                    }
                }

            }

        }
        Spacer(Modifier.height(8.dp))
    }
}
