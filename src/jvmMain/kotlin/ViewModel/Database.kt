package ViewModel

import dataClasses.HistoryData
import dataClasses.SavedPath
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId


class HistoryDataEntity() : RealmObject {
  @PrimaryKey
  var _id: ObjectId = ObjectId()
  var timeStamp: Long = 0
  var path: String = ""
  var mediaLength: String = "2 hr 35 min"
  var title: String = "Sample Title"
  var imgPath: String = "images/shrek.jpg"
  var percentWatched: Float = 0f
  var time: String = "00:00 PM"
  var date: String = "07 July 2023"
  var exists : Boolean = true
  constructor(timeStamp: Long,path: String,mediaLength: String,title: String,imgPath: String, percentWatched: Float,time: String,date: String,exists : Boolean ) : this() {
    this.timeStamp = timeStamp
    this.path = path
    this.mediaLength = mediaLength
    this.title = title
    this.imgPath = imgPath
    this.percentWatched = percentWatched
    this.time = time
    this.date = date
    this.exists = exists

  }
  fun toReadable() = HistoryData(this.timeStamp,this.path,this.mediaLength,this.title,this.imgPath, this.percentWatched,this.time,this.date ,this.exists)
}
val histDataConfig = RealmConfiguration.create(schema = setOf(HistoryDataEntity::class))
val histData = Realm.open(histDataConfig)

class MyPaths():RealmObject{
  @PrimaryKey
  var _id: ObjectId = ObjectId()
  var uniqueID :String = ""
  var path :String =""
  constructor(uniqueID:String,path:String):this(){
    this.uniqueID = uniqueID
    this.path = path
  }
  fun toReadable()= SavedPath(this.uniqueID , this.path)
}
val pathConfig = RealmConfiguration.create(schema = setOf(MyPaths::class))
val pathRealm = Realm.open(pathConfig)

class Item() : RealmObject {
  @PrimaryKey
  var _id: ObjectId = ObjectId()
  var name: String = ""
  var path: String = ""
  var ts:Long = 0
  constructor(name: String = "" , path:String = "" , ts:Long = 0) : this() {
    this.name = name
    this.ts = ts
    this.path = path
  }
}

val config = RealmConfiguration.create(schema = setOf(Item::class))
val realm: Realm = Realm.open(config)

fun temw(){
  realm.writeBlocking {
    copyToRealm(Item().apply {
      name = "MKX"
      ts = 33333
      path = "C:\\Users\\ujjawal\\Documents\\CPY_SAVES"
    })
  }
}

fun finddd(){
  val a : RealmResults<Item> = realm.query<Item>().find()
  a.forEach {
    Functions.log.info( it.name)
  }

}