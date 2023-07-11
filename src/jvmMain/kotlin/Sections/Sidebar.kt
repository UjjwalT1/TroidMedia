package Sections

import Theme.Colors.darkGrey
import Theme.Colors.red
import Theme.Fonts.KronaOne
import ViewModel.TroidViewModel
import androidx.compose.animation.*
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import dataClasses.SideBarItems


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SideBarButton(modifier:Modifier  = Modifier ,item : SideBarItems, viewModel: TroidViewModel  ){
    val curr = viewModel.curr
    val clrAnimated = animateColorAsState(if(curr== item.name) Color.White.copy(.1f) else Color.Transparent , tween())
    val heightAnimated by animateIntAsState(if(curr== item.name) 40 else 0 , tween())
    Box(Modifier.fillMaxWidth()){
        Column(verticalArrangement = Arrangement.Center , horizontalAlignment = Alignment.CenterHorizontally ,
            modifier = Modifier
                .clickable{ item.onClick() }
                .clip(RoundedCornerShape(4.dp)).fillMaxWidth()
                .background(clrAnimated.value).padding(2.dp)
        ){
            Spacer(Modifier.height(7.dp))
            Image(painter = painterResource("icons/${item.icon}") , contentDescription = null , Modifier.size(21.dp))
            Spacer(Modifier.height(7.dp))
            Text(item.name, fontFamily = KronaOne , fontSize = 10.sp , color = red )
            Spacer(Modifier.height(7.dp))

        }
        Spacer(modifier  = Modifier.height(heightAnimated.dp).align(Alignment.CenterEnd).background(red).width(3.dp))


    }

}

@Composable
fun Sidebar(viewModel: TroidViewModel){

    Column(horizontalAlignment = Alignment.CenterHorizontally , modifier = Modifier.background(color = darkGrey ).width(73.dp).fillMaxHeight()
       // .shadow(Color.Black.copy(alpha = 0.4f),4.dp,4.dp,9.dp,100.dp,100.dp)
    ){
        Spacer(Modifier.height(10.dp))
        viewModel.sideBarList.forEach {
            SideBarButton(item = it , viewModel = viewModel)
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.weight(1f))
        SideBarButton(item = viewModel.setting, viewModel = viewModel)
        Spacer(Modifier.height(20.dp))


    }
}