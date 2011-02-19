package code
package snippet

import code.model.{AutomatedTests}
import code.snippet.Param._

import scala.xml.{NodeSeq, Text, Elem}

import net.liftweb._
import util._
import common.Logger
import http._
import SHtml._
import S._
import js.JsCmds.{SetHtml, SetValueAndFocus}

import Helpers._

class TopMenu extends Logger {

  /**
    * Generate the Test Result view section
    */

  val showingVersion= versionString

  debug(showingVersion)
  

  def addVersionToLinks ={
    ClearClearable andThen
    "a [href+]" #> Text("/" + showingVersion) 
  }



}

