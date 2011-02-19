package code {
package snippet {

import code.model.{Series,Versions}

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


  /**
    * Generate the Series sidebar section
    */

  def series( xhtml: NodeSeq ) = {
    val seriesList: List[Series]= 
      Series.findAllFields(Seq[SelectableField] (Series. series_name), OrderBy(Series. series_name, Descending) )

    //  List(code.model.Versions={id=-1,version_name=2.3.0.1290})
    val series_name_col: List[String] = seriesList.map(_.series_name.is)
    //debug(series_name_col)

    def bindSeries(template: NodeSeq): NodeSeq = {
      //series_name_col.flatMap{ case (ser) => bind("series", template,"number" -> ("ss" + "ssa"))}
      series_name_col.flatMap{ case (ser) => bind("series", template,"number" ->
                                                      (ajaxButton(ser, 
                                                        () => {SetValueAndFocus("versionBox",{ser})}))
                                                  )}
    }
    bind("Series",xhtml, "list" -> bindSeries _)
  }

  

}

}
}
