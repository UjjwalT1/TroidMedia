package dataClasses

import ViewModel.MyPaths
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class PlayerSettings(): RealmObject {
    @PrimaryKey
    var _id: ObjectId = BsonObjectId()
    var seekSeconds:Int = 10000
    var forwardLength:Int = 5000
    var volumeChangeOnScroll:Int = 10 //5 unit inc or dec on each scroll

    constructor(seekSeconds:Int=10,forwardLength:Int =5, volumeChangeOnScroll:Int = 5):this(){
        this.seekSeconds = if(seekSeconds < 5) 5000 else if(seekSeconds >20) 20000 else seekSeconds*1000
        this.forwardLength = if(forwardLength < 5) 5000 else if(forwardLength >60) 60000 else forwardLength*1000
        this.volumeChangeOnScroll = if(volumeChangeOnScroll < 2) 2 else if(volumeChangeOnScroll >30) 30 else volumeChangeOnScroll
    }
    fun toReadable() = PlayerSetting(this.seekSeconds,this.forwardLength,this.volumeChangeOnScroll)
}

val playerObjConfig = RealmConfiguration.create(schema = setOf(PlayerSettings::class))
val playerRealm = Realm.open(playerObjConfig)


data class PlayerSetting( var seekSeconds:Int = 10000, var forwardLength:Int = 5000, var volumeChangeOnScroll:Int = 10 )