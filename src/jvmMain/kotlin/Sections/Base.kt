package Sections

import Functions.SearchStr
import Theme.Colors.grey
import ViewModel.TroidViewModel
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Base(viewModel: TroidViewModel){
    val scope  = rememberCoroutineScope()
    Row(Modifier.background(grey).fillMaxSize()){
        Sidebar(viewModel)
        Column {
            AnimatedContent(viewModel.curr){
                when(it){
                    "LIBRARY" -> Box(){
                        var query by remember { mutableStateOf("") }
                        Library(viewModel)
                        Search("Search All Media" , query ,
                            {
                                query = it;
                                if(it.isBlank()) viewModel.toggleLibView("Alphabet") else {
                                        viewModel.toggleLibView("")
                                        SearchStr(it , viewModel)
                                }
                            }

                        ){
                            Spacer(Modifier.width(30.dp))
                            TextButton(onClick =  { scope.launch(Dispatchers.IO) { viewModel.toggleLibView("def"); query="" } }
                                ,txt = if(viewModel.libraryView  == "Alphabet")"FOLDER VIEW" else if(viewModel.libraryView  == "Tree") "SORTED VIEW" else "SEARCH VIEW",modifier = Modifier.height(40.dp).padding(4.dp,0.dp).clip(
                                RoundedCornerShape(14.dp)
                            ),fSize = 17 , if(viewModel.libraryView  == "Alphabet")"icons/viewtype2.png" else if(viewModel.libraryView  == "Tree") "icons/layer2.png" else "icons/lens.png")
                        }
                    }
                    "HISTORY" -> Box(){ History(viewModel) }
                    "SETTING" -> Settings(viewModel)
                    "HOME" -> Box(){
                        var query by remember { mutableStateOf("") }
                        Home(viewModel)
                        Search("Search this folder", query , {query = it })
                    }
                }
            }

        }

    }

}




