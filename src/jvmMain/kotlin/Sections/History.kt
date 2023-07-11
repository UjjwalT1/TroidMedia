package Sections

import Theme.Colors.grey
import Theme.Colors.red
import Theme.Fonts.MonomaniacOne
import ViewModel.HistoryAccess
import ViewModel.TroidViewModel
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dataClasses.HistoryData

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun History(viewModel: TroidViewModel){
    val list = viewModel.histories.reversed() //viewModel.
    var clr by remember { mutableStateOf(false) }
    val color = animateColorAsState(if(clr)red.copy(.6f) else red.copy(.2f))
    if(list.isEmpty()){
        EmptyPage(viewModel,viewModel.searchStorage , "History Empty")
    } else
    LazyColumn(Modifier.fillMaxSize().padding(20.dp,2.dp)){
        item {
            Spacer(Modifier.height(10.dp))
            Row(Modifier.padding(20.dp)){
                Spacer(Modifier.weight(1f))
                IconButton(onClick = {viewModel.removeHistory()} , Modifier.size(40.dp).clip(RoundedCornerShape(50.dp)).background(color.value).onPointerEvent(PointerEventType.Enter){clr = true}.onPointerEvent(PointerEventType.Exit){clr = false}){
                    Image(painterResource("icons/trash.png"),contentDescription = null , Modifier.size(23.dp))
                }
            }
            Spacer(Modifier.fillMaxWidth().clip(RoundedCornerShape(40.dp)).background(red).height(5.dp))
            Spacer(Modifier.height(20.dp))
        }
        items(list){
            HistoryEntry({ viewModel.addHistory(HistoryData(path = it.path , mediaLength = it.mediaLength , title = it.title , imgPath = it.imgPath)) } , it )
        }
        item {
            Spacer(Modifier.height(45.dp))
        }

    }
}

@Composable
fun HistoryEntry(onClick: () -> Unit,data:HistoryData){
    Box(
        Modifier.height(110.dp)
            .padding(30.dp,5.dp,30.dp,2.dp)
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
                .background(Color.Black.copy(.84f)) ,
            verticalAlignment = Alignment.CenterVertically
        ){
            Spacer(Modifier.width(40.dp))
            Column (Modifier){
                Text(data.title , fontSize = 30.sp , fontFamily = MonomaniacOne, color = Color.White.copy(.85f) , maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.width(900.dp))
                Text("${data.mediaLength}  |  ${(data.percentWatched*100).toInt()}% Watched" , fontSize = 20.sp , fontFamily = MonomaniacOne, color = Color.White.copy(.65f))
                Spacer(Modifier.height(10.dp))
            }
            Spacer(Modifier.weight(1f))
            Column {
                Text(data.time, fontSize = 20.sp , color = red , fontFamily = MonomaniacOne)
                Text(data.date, fontSize = 20.sp , color = red , fontFamily = MonomaniacOne)
            }
            Spacer(Modifier.width(10.dp))
            IconButton(onClick = {}, Modifier.padding(50.dp,0.dp)){
                Image(painterResource("icons/play-button2.png"),contentDescription = null , Modifier.size(50.dp))
            }
        }
        Box(Modifier.align(Alignment.BottomCenter)){
            Spacer(Modifier.fillMaxWidth(1f).background(grey).height(4.dp))
            Spacer(Modifier.fillMaxWidth(data.percentWatched).background(red).height(4.dp))
        }


    }
}