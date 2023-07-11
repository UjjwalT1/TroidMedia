package ViewModel

import dataClasses.*
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryAccess{

    suspend fun addHistEntry(data:HistoryData){
            histData.write {
                copyToRealm(HistoryDataEntity().apply {
                    timeStamp = data.timeStamp
                    path = data.path
                    mediaLength = data.mediaLength
                    title = data.title
                    imgPath = data.imgPath
                    percentWatched = data.percentWatched
                    time = data.time
                    date = data.date
                    exists = data.exists
                })
            }

    }

    fun getHistory() : List<HistoryData>{
        val temp :RealmResults<HistoryDataEntity> = histData.query<HistoryDataEntity>().find()
        return temp.toList().map {
            it.toReadable()
        }
    }

    fun getLatest() : HistoryData? {
        return histData.query<HistoryDataEntity>().find().firstOrNull()?.toReadable()
    }

    suspend  fun clearHistory(list:List<Long> = listOf()){
       // CoroutineScope(Dispatchers.IO).launch{
            histData.write {
                if(list.isNotEmpty())
                list.forEach {
                    val temp = this.query<HistoryDataEntity>("timeStamp == '$it'").find().firstOrNull()
                    if(temp!=null)delete(temp)
                }
                else deleteAll()
            }
      //  }
    }

    suspend fun updateHistory(ts:Long , exists:Boolean){
            histData.write {
                val temp = this.query<HistoryDataEntity>("timeStamp == '$ts'").find().firstOrNull()
                if(temp!=null) temp.exists = exists

            }
    }
    suspend fun updateHistory(ts:Long , percentWatched:Float){
        histData.write {
            val temp = this.query<HistoryDataEntity>("timeStamp == '$ts'").find().firstOrNull()
            if(temp!=null) temp.percentWatched = percentWatched
        }
    }
    suspend fun updateHistory(ts:Long , image:String){
        histData.write {
            val temp = this.query<HistoryDataEntity>("timeStamp == '$ts'").find().firstOrNull()
            if(temp!=null) temp.imgPath = image

        }
    }

}

class PathAccess{

    suspend fun addPath(data: SavedPath){
        //CoroutineScope(Dispatchers.IO).launch{
            pathRealm.write {
                copyToRealm(MyPaths().apply {
                    uniqueID = data.uniqueID
                    path = data.path
                })
            }
      //  }
    }

    fun getPaths() : List<SavedPath>{
        val temp  = pathRealm.query<MyPaths>().find()
        return temp.toList().map {
            it.toReadable()
        }
    }

    suspend fun updatePath(uniqueID:String, newValue:String){
      //  CoroutineScope(Dispatchers.IO).launch{
            pathRealm.write {
                val temp  = this.query<MyPaths>("uniqueID == '$uniqueID'").find().firstOrNull()
                if(temp!= null) temp.path = newValue
            }
      //  }

    }

    suspend fun removePath(uniqueI:String){
      //  CoroutineScope(Dispatchers.IO).launch{
            pathRealm.write {
                try {
                    val temp = this.query<MyPaths>("uniqueID == '$uniqueI' ").find().first()
                    delete(temp)
                }catch (_:Exception){}


       //     }
        }
    }

}

class PlayerSettingAccess{

    suspend fun createSetting(data : PlayerSettings){
        playerRealm.write {
            copyToRealm(PlayerSettings().apply {
                seekSeconds = data.seekSeconds
                forwardLength = data.forwardLength
                volumeChangeOnScroll = data.volumeChangeOnScroll
            })
        }
    }

    suspend fun setSetting(data : PlayerSettings){
        playerRealm.write {
            val temp  = this.query<PlayerSettings>().find().first()
            temp.seekSeconds = data.seekSeconds
            temp.forwardLength = data.forwardLength
            temp.volumeChangeOnScroll = data.volumeChangeOnScroll
        }
    }

    fun getSetting(): PlayerSettings?{
        return playerRealm.query<PlayerSettings>().find().firstOrNull()

    }
}