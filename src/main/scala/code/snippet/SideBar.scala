package code {
package snippet {

import code.model.{Versions}

import _root_.scala.xml.{NodeSeq, Text}

import _root_.net.liftweb._
import util._
import common.Logger
import mapper.{OrderBy, Descending, SelectableField}
import http.SHtml._
import http.js.JsCmds.{SetHtml, SetValueAndFocus}

import _root_.java.util.Date
import code.lib._
import Helpers._

class SideBar extends Logger {

  /**
    * Generate the Versions sidebar section
    */


  def versions( xhtml: NodeSeq ): NodeSeq = {
    versions("%", xhtml)
  }



  def versions( prefix: String, xhtml: NodeSeq ) = {
    val version_name_col: List[String]= Versions.getVersionList(prefix)
    //debug(version_name_col)

    def bindConsumption(template: NodeSeq): NodeSeq = {
      version_name_col.flatMap{
        case (ver) => bind("version", template, "number" ->
          link("/index/" + ver, () => {}
          , <div>{ver}</div>)
        )


      }
    }
    bind("Versions",xhtml, "list" -> bindConsumption _)
  }



}

}
}
