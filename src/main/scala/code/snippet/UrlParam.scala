package code 
package snippet 

import net.liftweb._
import util._
import common.Logger
import common.Full
import sitemap._
import Helpers._
import Loc._

// capture the page parameter information
case class ParamInfo(version: String)

// a snippet that takes the page parameter information
class UrlParam(pi: ParamInfo)  {
  def render = "*" #> pi.version
}

object Param extends Logger{
  // Create a menu for /index/<version>

  val BrowserDetailsMenu= Menu.param[ParamInfo]("Browser", "Browsers Details" ,
                                   s => Full(ParamInfo(s)),
                                   pi => pi.version) / "browser-details"


  lazy val browserLoc= BrowserDetailsMenu.toLoc

  

  def render = "*" #> browserLoc.currentValue.map(_.version)

  def browserVersionString= browserLoc.currentValue.map(_.version)

  def versionString= browserVersionString openOr("")

}


