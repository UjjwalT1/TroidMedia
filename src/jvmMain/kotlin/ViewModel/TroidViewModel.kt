package ViewModel

import Functions.SavedMedia
import androidx.compose.runtime.*
import dataClasses.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Stack

class TroidViewModel(private val historyAccess: HistoryAccess, private val pathAccess: PathAccess , private val playerSettingAccess: PlayerSettingAccess){

    var curr by  mutableStateOf("HOME")
    fun setCurrr(str:String){
        curr = str
    }
    val sideBarList = listOf(
        SideBarItems(icon = "home2.png", name = "HOME", onClick = { setCurrr("HOME"); }),
        SideBarItems(icon = "library2.png", name = "LIBRARY", onClick = { setCurrr("LIBRARY"); }),
        SideBarItems(icon = "history2.png", name = "HISTORY", onClick = { setCurrr("HISTORY"); })
    )
    val setting  = SideBarItems(icon = "setting2.png", name = "SETTING", onClick = {setCurrr("SETTING");})

    val paths = mutableStateListOf<SavedPath>() //"D:\\Photos & Videos\\CRT" , "D:\\Photos & Videos\\Tutorials","D:\\Photos & Videos\\Songs"
    var paths2 = mutableListOf<SavedPath>()


    var pathListRefresh by  mutableStateOf("VIEW")
    fun setPathListRef(str:String){
        pathListRefresh = str
    }

    var tree = Node("Dir",0 ,"")
    var pathIndex = mutableMapOf<String,MutableList<MediaData>>()
    var nameIndex = mutableMapOf<String,MediaData>()
    var alphabetIndex = mutableMapOf<Char,MutableList<MediaData>>()
    var treeState by mutableStateOf(tree)
    var dirStk : Stack<Node> = Stack()  //Stack to store that previous path
    fun setTreeStat(node:Node?){
        if(node == null) {
            if(!dirStk.empty()){
                treeState = dirStk.peek()
                dirStk.pop()
            }
        }
        else {
            dirStk.push(treeState)
            treeState = node
        }
    }

    fun resetPathAndCompany(){
        tree = Node("Dir",0 ,"")
        pathIndex = mutableMapOf()
        nameIndex = mutableMapOf()
        alphabetIndex = mutableMapOf()
        dirStk = Stack()
    }

    var libraryView by mutableStateOf("Tree")
    fun toggleLibView(str:String){
        libraryView = if(str == "def") {
            if (libraryView == "Alphabet") "Tree" else "Alphabet"
        }
        else str
    }
    var searchStorage = mutableStateListOf<MediaData>()

    var pathIfVideoClicked = mutableStateOf<HistoryData?>(null)
    var histories = mutableStateListOf<HistoryData>()
    fun addHistory(data:HistoryData , exceptio : Boolean = false){
        CoroutineScope(Dispatchers.IO).launch{
            latestForGigaTileBackUp = latestForGigaTile
            latestForGigaTile = data
            if(!exceptio)pathIfVideoClicked.value = data
            histories.add(data)
            historyAccess.addHistEntry(data)
        }
    }

    fun removeHistory(){
        CoroutineScope(Dispatchers.IO).launch{
            histories.clear()
            historyAccess.clearHistory()
        }
    }
    var netDiff by mutableStateOf(0)
    var latestForGigaTileBackUp:HistoryData? = null
    var latestForGigaTile by mutableStateOf<HistoryData?>(null)
    fun updateHistory(value : Any){
        CoroutineScope(Dispatchers.IO).launch{
            if(value is Boolean){
                historyAccess.updateHistory(pathIfVideoClicked.value!!.timeStamp ,value)
            }else if(value is Float){

                historyAccess.updateHistory(pathIfVideoClicked.value!!.timeStamp ,value)
            }
        }
    }
    fun refreshHistory(){
        histories.clear()
        histories.addAll(historyAccess.getHistory())
        latestForGigaTile = histories.lastOrNull()
    }
    init {
        updatePaths()
    }


    fun updatePaths(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                histories.addAll(historyAccess.getHistory())
                paths2 = pathAccess.getPaths().toMutableList()
                paths.clear()
                paths.addAll(paths2)
                resetPathAndCompany()
                SavedMedia(this@TroidViewModel , tree)
                treeState = tree
                latestForGigaTile = histories.lastOrNull()

                getConfig()
            }catch (e:Exception){
                println("At line 95 ViewModel ${e.stackTrace}")
            }

        }
    }

    fun crudPath(lvl:Int , uniqueID:String , path:String){
        setPathListRef("")
        CoroutineScope(Dispatchers.IO).launch {
            if(lvl == 0 ){
                async { pathAccess.updatePath(uniqueID,path)  }.await()
                updatePaths()
                setPathListRef("VIEW")
            }else if(lvl == 1 ){
                async { pathAccess.removePath(uniqueID) }.await()
                updatePaths()
                setPathListRef("VIEW")
            }else if(lvl == 2){
                async { pathAccess.addPath(SavedPath(path = path)) }.await()
                updatePaths()
                setPathListRef("VIEW")
            }

        }

    }

    var job:Job? = null
    var overlay by mutableStateOf(false)
    fun setOverlay(){
        overlay = true
        job = CoroutineScope(Dispatchers.IO).launch{
            delay(4000)
            overlay = false
        }
    }
    fun resetOverlay(){
        overlay = false
    }

    val playerConfiguration = mutableStateOf(PlayerSettings())
    fun saveConfig(data:PlayerSettings){
        playerConfiguration.value = data
        CoroutineScope(Dispatchers.IO).launch {
            playerSettingAccess.setSetting(data)
        }
    }
    fun getConfig(){
        val temp = playerSettingAccess.getSetting()
        if(temp == null){
            playerConfiguration.value = PlayerSettings()
            CoroutineScope(Dispatchers.IO).launch {
                playerSettingAccess.createSetting(PlayerSettings())
            }
        }else
            playerConfiguration.value = temp

    }

    var currentVolume by mutableStateOf(0)
    var isMute by mutableStateOf(false)

    var seek by mutableStateOf<String>("")
    var seekJob:Job? = null
    fun seekSet(str:String){
        seek = str
        seekJob = CoroutineScope(Dispatchers.IO).launch {
            if(str == "Volume")delay(3000)
            else delay(500)
            seek = ""
        }
    }


//    init {
//        getConfig()
//    }

}