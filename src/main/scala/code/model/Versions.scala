package code {
package model {

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._

/**
 * The singleton that has methods for accessing the database
 */


object Versions extends Versions with LongKeyedMetaMapper[Versions] {
  override def dbTableName = "versions" // define the DB table name
  def getVersionList(series: String): List[String]= 
      Versions.findAllFields(Seq[SelectableField] (Versions.version_name), 
          OrderBy(Versions.version_name, Descending),
          Like(Versions.version_name, series + "%")
      ).map(_.version_name.is)

}


class Versions extends LongKeyedMapper[Versions] with OneToMany[Long, Versions] {
  def getSingleton = Versions
  
  def primaryKeyField = id
  object id extends MappedLongIndex(this)
  object version_name extends MappedString(this, 15) {
    override def dbIndexed_? = true
  }
  

}
}
}
