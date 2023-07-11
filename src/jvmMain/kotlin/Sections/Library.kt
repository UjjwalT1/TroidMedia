package Sections

import Theme.Colors.clrTheme
import Theme.Colors.grey
import Theme.Colors.red
import Theme.Fonts.Hind
import Theme.Fonts.MonomaniacOne
import ViewModel.TroidViewModel
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dataClasses.HistoryData
import dataClasses.MediaData
import dataClasses.extensionToType
import java.io.File

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Library(viewModel: TroidViewModel){
    val tree = viewModel.treeState
    val view = viewModel.libraryView
    val searchList = viewModel.searchStorage

    Column(Modifier.fillMaxSize().padding(20.dp,2.dp)){
        Spacer(Modifier.height(40.dp))
        AnimatedContent(view){
            when(it){
                "Alphabet" ->{
                    if(viewModel.alphabetIndex.isEmpty()) EmptyPage(viewModel, searchList , "First add a folder that contains an audio or a video.",
                        "Setting -> Add Path -> Select -> (select the directory) -> Save Path",Color.White.copy(.6f),22 )
                    else
                        Column {
                            Spacer(Modifier.height(35.dp))
                            LazyColumn {
                                viewModel.alphabetIndex.toSortedMap().forEach { (key, mediaList) ->
                                    item {
                                        BarLetter(key.toString())
                                    }

                                    items(mediaList.chunked(3)){
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround){
                                            it.forEach {
                                                Tiles(viewModel,"23 min | ${it.size}" , it.name,if(extensionToType[it.mediaExtension]=="Video")"images/video.png" else "images/audio.png",it.path
                                                ) {
                                                    viewModel.addHistory(
                                                        HistoryData(path = it.path, mediaLength = "XX hr XX min",title = it.name )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                }
                "Tree" -> {
                    if(tree.mediaList.size ==0 && tree.subDir.size ==0) EmptyPage(viewModel, searchList,"First add a folder that contains an audio or a video",
                        "GOTO: Setting -> Add Path -> Select -> (select the directory) -> Save Path",Color.White.copy(.6f) , 22 )
                    else
                    Column {
                        Spacer(Modifier.height(30.dp))
                        Row(verticalAlignment = Alignment.CenterVertically){
                            Spacer(Modifier.width(20.dp))
                            TextButton({viewModel.setTreeStat(null)} , "BACK" , fSize = 18 , imgPath = "icons/previous2.png")
                            Spacer(Modifier.width(20.dp))
                            Text(tree.curr,color = Color.White.copy(.7f) , fontSize = 28.sp , fontFamily = Hind, fontWeight = FontWeight.Black)
                        }
                        Spacer(Modifier.height(15.dp))
                        Spacer(Modifier.fillMaxWidth().background(red).height(3.dp).clip(RoundedCornerShape(5.dp)))

                        AnimatedContent(tree){ tre ->
                            LazyVerticalGrid(columns = GridCells.Adaptive(350.dp) , modifier = Modifier, contentPadding = PaddingValues(60.dp,4.dp)
                            ){
                                itemsIndexed(tre.subDir){a,it->
                                    Folder(File(it.curr).name , it.subDir.size , it.mediaList.size) {  viewModel.setTreeStat(it) }
                                }
                                items(tre.mediaList){
                                    Tiles(viewModel,"23 min | ${it.size}" , it.name, if(extensionToType[it.mediaExtension]=="Video")"images/video.png" else "images/audio.png",it.path
                                    ) {
                                        viewModel.addHistory(
                                            HistoryData(path = it.path, mediaLength = "XX hr XX min",title = it.name )
                                        )
                                    }
                                }
                            }
                        }
                    }


                }
                else -> { EmptyPage(viewModel, searchList)
//                    Column {
//                        Spacer(Modifier.height(30.dp))
//                        if(searchList.size ==0){
//                            Column(Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally , verticalArrangement = Arrangement.Center){
//                                Image(painterResource("icons/blank.png"),contentDescription = null , Modifier.size(250.dp).padding(0.dp,0.dp,0.dp,20.dp))
//                                Text("NOTHING FOUND", color = red , fontFamily = MonomaniacOne , fontSize = 30.sp)
//                            }
//
//                        }else
//                        LazyVerticalGrid(columns = GridCells.Adaptive(350.dp) , modifier = Modifier, contentPadding = PaddingValues(60.dp,4.dp)
//                        ){
//                            items(searchList){
//                                Tiles(viewModel,"Search min | ${it.size}" , it.name,"images/diamond.jpg",{})
//                            }
//                        }
//
//                    }
                }

            }
        }


    }
}

@Composable
fun EmptyPage(viewModel: TroidViewModel,searchList:List<MediaData> ,
              string:String ="NOTHING FOUND" ,  string2:String ="" ,
              color:Color = red , size :Int = 30 , font:FontFamily = MonomaniacOne){
    Column {
        Spacer(Modifier.height(30.dp))
        if(searchList.isEmpty()){
            Column(Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally , verticalArrangement = Arrangement.Center){
                Image(painterResource("icons/blank.png"),contentDescription = null , Modifier.size(250.dp).padding(0.dp,0.dp,0.dp,20.dp))
                Text(string, color = color , fontFamily = MonomaniacOne , fontSize = 30.sp)
                Text(string2, color = red , fontFamily = font , fontSize = size.sp , letterSpacing = 1.sp)


            }

        }else
            LazyVerticalGrid(columns = GridCells.Adaptive(350.dp) , modifier = Modifier, contentPadding = PaddingValues(60.dp,4.dp)
            ){
                items(searchList){
                    Tiles(viewModel,"23 min | ${it.size}" , it.name,if(extensionToType[it.mediaExtension]=="Video")"images/video.png" else "images/audio.png",it.path
                    ) {
                        viewModel.addHistory(
                            HistoryData(path = it.path, mediaLength = "XX hr XX min",title = it.name )
                        )
                    }
                }
            }

    }
}

@Composable
fun BarLetter(char: String){
    Column (Modifier.padding(30.dp,10.dp).fillMaxWidth()){
        Spacer(Modifier.height(10.dp))
        Row(){
            Spacer(Modifier.weight(1f))
            Text(char, fontSize = 26.sp , fontFamily = MonomaniacOne , color = red)
        }
        Spacer(Modifier.fillMaxWidth().background(red).height(3.dp).clip(RoundedCornerShape(5.dp)))
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
fun Folder(title:String , folders:Int , media:Int , onClick:()->Unit){
    val content  = "$folders Folders | $media Media"
    Column(
        Modifier.padding(10.dp).shadow(Color.Black.copy(.6f),3.dp,3.dp , 12.dp,20.dp,20.dp).width(400.dp)
            .clip(RoundedCornerShape(14.dp)).background(grey).clickable {
                onClick()
            }, horizontalAlignment = Alignment.CenterHorizontally
    ){

        Image(
            painterResource("icons/folder2.png"),contentDescription = null , contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth(.9f).height(200.dp).padding(0.dp,10.dp,0.dp,0.dp).clip(RoundedCornerShape(14.dp))
        )

        Row(Modifier, verticalAlignment = Alignment.CenterVertically ){
            Column (Modifier.padding(30.dp,4.dp,0.dp ,0.dp).fillMaxWidth(.8f)){
                Text(title , fontFamily = Hind, fontSize = 21.sp , color = Color.White , overflow = TextOverflow.Ellipsis , maxLines = 1 )
                Text( content , fontFamily = MonomaniacOne, fontSize = 18.sp , color =  Color.White.copy(.4f) , letterSpacing = 1.sp)
            }
            Spacer(Modifier.weight(1f))

//            Box(){
//                IconButton(onClick = {drop = true}, Modifier.padding(20.dp,0.dp).size(50.dp)){
//                    Image(painterResource("icons/ellipsis2.png"),contentDescription = null , Modifier.size(20.dp))
//                }
//                MaterialTheme(shapes = Shapes(medium = RoundedCornerShape(12.dp) , large = RoundedCornerShape(20.dp) , small = RoundedCornerShape(7.dp) ),
//                    colors = clrTheme
//                ){
//                    DropdownMenu(drop , {drop = false} , modifier = Modifier.width(150.dp)){
//                        Item("DELETE","icons/trash.png",{})
//                        Item("STREAM","icons/radio2.png",{})
//                    }
//                }
//
//            }

        }
        Spacer(Modifier.height(8.dp))
    }
}