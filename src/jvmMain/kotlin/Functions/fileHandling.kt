package Functions

import ViewModel.TroidViewModel
import dataClasses.MediaData
import dataClasses.Node
import dataClasses.extensionToType
import java.awt.Dialog
import java.awt.FileDialog
import java.io.File
import java.util.logging.Logger
import javax.swing.JFileChooser
import java.text.SimpleDateFormat
import java.util.*


val log = Logger.getLogger("Status :")



fun getDate(timestamp: Long): Pair<String, String> {
    val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val sdfDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    val time = sdfTime.format(Date(timestamp))
    val date = sdfDate.format(Date(timestamp))
    return Pair(time, date)
}

fun openFolder():String? {
    val fileChooser = JFileChooser()
    fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
    val result = fileChooser.showOpenDialog(null)

    if (result == JFileChooser.APPROVE_OPTION) {
        val selectedFolder = fileChooser.selectedFile
        val folderPath = selectedFolder.absolutePath
       // println("Selected Folder Path: $folderPath")
        return folderPath
    }
    return null
}


fun openFilePicker():String{
    val dialog = FileDialog(null as Dialog?)
    dialog.title = "Select File"
    dialog.mode = FileDialog.LOAD
    dialog.isVisible = true

    val selectedFile = File(dialog.directory)
    val filePath = selectedFile.absolutePath

    // Do something with the file path
    return filePath
}



class SavedMedia(viewModel: TroidViewModel , dirs:Node ){
    fun fileSize(sizeInBytes: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB")
        var size = sizeInBytes.toDouble()
        var unitIndex = 0

        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }

        val formattedSize = String.format("%.2f", size)
        return "$formattedSize ${units[unitIndex]}"
    }

    fun getFileExtension(fileName: String): String? {
        val dotIndex = fileName.lastIndexOf(".")
        return if (dotIndex > 0 && dotIndex < fileName.length - 1) {
            fileName.substring(dotIndex + 1)
        } else {
            null
        }
    }

    fun makeTree(curr:File, level:Int, dir:Node ,  pathIndex:MutableMap<String,MutableList<MediaData>> , nameIndex:MutableMap<String,MediaData> , alphabetIndex:MutableMap<Char,MutableList<MediaData>> ){

        curr.listFiles()?.forEach {
            if(it.isDirectory){
                val child = Node("Dir" , level+1 , it.path)
                dir.subDir.add(child)
                makeTree(it , level+1 , child , pathIndex, nameIndex , alphabetIndex)
            }else if(extensionToType.containsKey("."+getFileExtension(it.name))){
                val extn = "."+getFileExtension(it.name)
                val temp = MediaData(it.name,it.path,fileSize(it.length()),extn)
                dir.mediaList.add(temp)
                nameIndex[it.name] = temp //TODO Indexing nameWise
                //TODO Indexing letterWise
                if(alphabetIndex.containsKey(it.name[0].uppercaseChar())){
                    alphabetIndex[it.name[0].uppercaseChar()]!!.add(temp)
                }else {
                    alphabetIndex[it.name[0]] = mutableListOf(temp)
                }
                //TODO Indexing pathWise
                if(pathIndex.containsKey(it.parent)){
                    pathIndex[it.parent]!!.add(temp)
                }else {
                    pathIndex[it.parent]  = mutableListOf(temp)
                }
            }

        }
    }

    init {
        try {
            viewModel.paths2.map { it.path }.forEach {
                val temp = Node("Dir" , 1 , it )
                dirs.subDir.add(temp)
                makeTree(File(it),1 , temp , viewModel.pathIndex, viewModel.nameIndex  ,viewModel.alphabetIndex )
            }
        }catch (e:Exception){
            println(e.message)
        }


    }
}


fun SearchStr(query:String , viewModel: TroidViewModel){
    var a = viewModel.searchStorage
    a.clear()
    try{
        viewModel.nameIndex.keys.forEach {
            if(it.lowercase().contains(Regex(query.lowercase()))){
                a.add(viewModel.nameIndex[it]!!)
            }
        }
    }catch (_:Exception){  }


}


fun WriteText(str:String){
    val logs = File("ScreenShots/Logs.txt")
    if(logs.exists()) logs.appendText("$str \n")
    else{
        File("ScreenShots").mkdir()
        logs.createNewFile()
        logs.appendText("$str \n")
    }
}
