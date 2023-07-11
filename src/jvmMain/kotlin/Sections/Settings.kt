package Sections

import Functions.openFilePicker
import Functions.openFolder
import Theme.Colors.darkGrey
import Theme.Colors.grey
import Theme.Colors.red
import Theme.Fonts.Hind
import Theme.Fonts.MonomaniacOne
import Theme.Spinner
import ViewModel.TroidViewModel
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dataClasses.PlayerSettings
import dataClasses.SavedPath

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Settings(viewModel: TroidViewModel){
    var currentConfig = viewModel.playerConfiguration.value
    var seekTime by remember { mutableStateOf(currentConfig.seekSeconds/1000) }
    var fwdLength by remember { mutableStateOf(currentConfig.forwardLength/1000) }
    var volInc by remember { mutableStateOf(currentConfig.volumeChangeOnScroll) }
    val list = viewModel.paths
    var add by remember { mutableStateOf(false) }
    Column {
        Spacer(Modifier.height(30.dp))
        Box {
            Column {
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally , modifier = Modifier.padding(50.dp,10.dp)) {

                    DarkCard(pH = 26 , pV = 12 ){
                        Row( verticalAlignment = Alignment.CenterVertically){
                            Spacer(Modifier.width(20.dp))
                            Column {
                                MainTxt("Only the folders added here are scanned for media . ")
                                SubTxt("Please don’t add folders that contains miscellaneous data ( Eg: Adding the whole drive ) as it may slow down the search .\nAdd those folders where videos / audios are in abundace . ")
                            }
                            Spacer(Modifier.weight(1f))
                            SimpleButton({ add = true },"ADD PATH")
                            Spacer(Modifier.width(20.dp))
                        }
                        Spacer(Modifier.height(10.dp))
                        when(viewModel.pathListRefresh){
                            "VIEW" -> list.forEachIndexed{index,it ->
                                var query by remember { mutableStateOf(it.path) }
                                DarkCard(pH = 10 , pV = 6 ){
                                    Row( verticalAlignment = Alignment.CenterVertically){
                                        CustomTextField(Modifier.fillMaxWidth(.8f).height(40.dp).padding(50.dp,0.dp,50.dp,0.dp)
                                            .clip(RoundedCornerShape(14.dp)).background(red.copy(alpha = 0.1f)),
                                            value = query , onValueChange = { query = it} , placeholder = it.path, leadingIcon = {
                                                Spacer(Modifier.width(10.dp))
                                            } , enabled = true,
                                            trailingIcon = {
                                                if(it.path != query){
                                                    IconButton(onClick = { viewModel.crudPath(0, it.uniqueID , query) /*TODO Updating the path entered*/}){
                                                        Image(painterResource("icons/check.png"),contentDescription = null, Modifier.size(25.dp))
                                                    }
                                                }
                                            }
                                        )
                                        SimpleButton({viewModel.crudPath(1,it.uniqueID ,"Removing path");  } ,"REMOVE", Modifier.padding(4.dp,0.dp).clip(RoundedCornerShape(14.dp)).size(100.dp,40.dp),16)
                                    }
                                }
                            }
                            else -> DarkCard(pH = 10 , pV = 6 ){
                                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally , modifier = Modifier.fillMaxWidth().size(100.dp)) {
                                    Spinner(50.dp)
                                }
                            }
                        }
                    }
                }
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally , modifier = Modifier.padding(50.dp,10.dp)) {

                    DarkCard(pH = 26 , pV = 12 ){
                        Row( verticalAlignment = Alignment.CenterVertically){
                            Spacer(Modifier.width(20.dp))
                            Column {
                                MainTxt("Keyboard Forward/Backward seek")
                                SubTxt("Seconds to skip each time when Left/Right button on keyboard is pressed.\nDefault is 5s.  |  Range 5s to 60s ")
                            }
                            Spacer(Modifier.weight(1f))
                            ButtonInput({fwdLength = try{it.toInt()}catch (_:Exception){0} } , fwdLength)
                            Spacer(Modifier.width(30.dp))
                            AnimatedContent(currentConfig.forwardLength/1000 != fwdLength){
                                if(it)SimpleButton({ viewModel.saveConfig(PlayerSettings(
                                    forwardLength = fwdLength,
                                    seekSeconds = currentConfig.seekSeconds/1000,
                                    volumeChangeOnScroll = currentConfig.volumeChangeOnScroll)) },
                                    "SAVE",colorB = red , colorT = grey)
                                else SimpleButton({ },"SAVE")
                            }

                            Spacer(Modifier.width(20.dp))
                        }

                    }
                }
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally , modifier = Modifier.padding(50.dp,10.dp)) {

                    DarkCard(pH = 26 , pV = 12 ){
                        Row( verticalAlignment = Alignment.CenterVertically){
                            Spacer(Modifier.width(20.dp))
                            Column {
                                MainTxt("Seek Seconds")
                                SubTxt("Seconds to skip when circular arrow button besied the Pause/Play is clicked .\n" +
                                        "Default is 10s.  |  Range 5s to 20s ")
                            }
                            Spacer(Modifier.weight(1f))
                            ButtonInput({seekTime = try{it.toInt()}catch (_:Exception){0} } , seekTime)
                            Spacer(Modifier.width(30.dp))
                            AnimatedContent(currentConfig.seekSeconds/1000 != seekTime){
                                if(it)SimpleButton({ viewModel.saveConfig(PlayerSettings(seekSeconds = seekTime,forwardLength = currentConfig.forwardLength/1000, volumeChangeOnScroll = currentConfig.volumeChangeOnScroll)) },"SAVE",colorB = red , colorT = grey)
                                else SimpleButton({ },"SAVE")
                            }
                            Spacer(Modifier.width(20.dp))
                        }

                    }
                }
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally , modifier = Modifier.padding(50.dp,10.dp)) {

                    DarkCard(pH = 26 , pV = 12 ){
                        Row( verticalAlignment = Alignment.CenterVertically){
                            Spacer(Modifier.width(20.dp))
                            Column {
                                MainTxt("Scroll Volume Change")
                                SubTxt("The % of volume to change when scolled.\n" +
                                        "Default is 10%.  |  Range 2%  to 30% ")
                            }
                            Spacer(Modifier.weight(1f))
                            ButtonInput({volInc = try{it.toInt()}catch (_:Exception){0} } , volInc)
                            Spacer(Modifier.width(30.dp))
                            AnimatedContent(currentConfig.volumeChangeOnScroll != volInc){
                                if(it)SimpleButton({ viewModel.saveConfig(PlayerSettings(volumeChangeOnScroll = volInc , seekSeconds = currentConfig.seekSeconds/1000, forwardLength = currentConfig.forwardLength/1000)) },"SAVE",colorB = red , colorT = grey)
                                else SimpleButton({ },"SAVE")
                            }
                            Spacer(Modifier.width(20.dp))
                        }

                    }
                }

            }
            if(add)
                ConfirmDialog(onYes = {
                    viewModel.crudPath(2 , "Adding new Path" , it); add = false }, onNo = {add = false},"Add Path", "Please try not to add folders that contains more miscellenious content that the media files. Stay Organised.",add)

        }


    }




}

@Composable
fun DarkCard(modifier:Modifier = Modifier, pH:Int = 20,pV:Int = 10,content:@Composable() (ColumnScope.() -> Unit) ){
    Column(modifier.clip(RoundedCornerShape(30.dp)).background(darkGrey).padding(pH.dp,pV.dp).animateContentSize(),
        verticalArrangement = Arrangement.Center){
        content()
    }
    Spacer(Modifier.height(5.dp))
}

@Composable
fun SimpleButton(onClick:()->Unit  ={  openFolder()} ,txt:String= "ADD PATH",modifier:Modifier = Modifier.padding(4.dp,1.dp), fSize:Int = 19 , colorB:Color = grey, colorT:Color = red){
    Button(onClick = onClick, modifier = modifier.border(1.dp,red, RoundedCornerShape(14.dp)).clip(RoundedCornerShape(14.dp)) , colors = ButtonDefaults.buttonColors(
        colorB)){
        Column {
            Text(txt, color = colorT , fontFamily = MonomaniacOne , fontSize = fSize.sp)
            Spacer(Modifier.height(5.dp))
        }

    }
}

@Composable
fun MainTxt(txt:String){
    Text(txt, color = Color.White , fontFamily = Hind , fontSize = 20.sp)
}
@Composable
fun SubTxt(txt:String){
    Text(txt, color =Color.White.copy(.5f) , fontFamily = Hind , fontSize = 14.sp , lineHeight = 10.sp)
}

@Composable
fun ButtonInput(onValueChange:(String)->Unit ,value:Int,modifier:Modifier = Modifier.padding(4.dp,1.dp), fSize:Int = 19 , colorB:Color = grey, colorT:Color = Color.White){
    Button(onClick = {}, modifier = modifier.border(1.dp,red, RoundedCornerShape(14.dp)).clip(RoundedCornerShape(14.dp)) , colors = ButtonDefaults.buttonColors(
        colorB)){
        Column {
            BasicTextField(
                value = if(value == 0)"" else value.toString(),
                onValueChange = {

                    onValueChange(it)
                },
                textStyle = TextStyle(color = colorT , fontFamily = Hind , fontSize = 18.sp),
                modifier = Modifier.width(50.dp)
            )
        }

    }
}