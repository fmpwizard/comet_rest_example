package code
package snippet

import code.model.{Versions}
import code.snippet.Param._
import scala.xml.{NodeSeq, Text, Elem}
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http.S
import net.liftweb.http.SHtml._
import net.liftweb.http.js.JsCmd
import _root_.net.liftweb.http.js.JE.JsRaw
import _root_.net.liftweb.http.js.jquery.JqJsCmds._
import _root_.net.liftweb.mapper.{OrderBy, Descending, SelectableField}
import _root_.net.liftweb.widgets.autocomplete.AutoComplete
import _root_.java.util.Date
import code.lib._
import Helpers._

class VersionAutoComplete extends Logger {

  /**
    * Generate the Versions sidebar section
    */

  
  val verListName: String= S.attr("id_verList") openOr "versionList"

  val showingVersion= versionString
  debug(showingVersion)
    
  def show(xhtml: NodeSeq): NodeSeq ={
    bind("ajax", xhtml, "text" -> doText _)
  }

    
  def ajaxLiveText(value: String, func: String => JsCmd, attrs: (String, String)*): Elem = { 
        S.fmapFunc(S.SFuncHolder(func)) {funcName => 
            (attrs.foldLeft(<input id="versionBox" 
type="text" 
value={showingVersion}
onfocus="if (this.value == 'Enter version number') {this.value = '';}"   
onblur="if (this.value == '') {this.value = 'Enter version number';}"
/>)(_ % _)) % 
                    ("onkeyup" -> makeAjaxCall(
                        JsRaw("'" + 
                            funcName + 
                            "=' + " +
                            "encodeURIComponent(this.value)")
                            )

                    ) 

 
        } 
    }
  val sideBar = new SideBar
      
  def doText(msg: NodeSeq) =
      ajaxLiveText("", versionPrefix => JqSetHtml(verListName, 
          sideBar.versions( versionPrefix , <Versions:list>
<tr>
  <td> <version:number /> </td>
</tr></Versions:list>)) )
  


  

}

