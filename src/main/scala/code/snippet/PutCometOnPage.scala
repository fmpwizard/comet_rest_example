package code
package snippet

import code.snippet.Param._

import scala.xml.NodeSeq

import net.liftweb._
import util._
import actor._
import http._
import Helpers._
import common.Full

/**
  * This object adds a ComeActor of type BrowserDetails with a name == version it displays
  * This allows having multiple tabs open displaying different version results
  */
object PutCometOnPage {
  def render(xhtml: NodeSeq): NodeSeq = {
    //val id = Helpers.nextFuncName
    val id= "browser-details" + versionString
    debug("Using CometActor with name: %s".format(id))
    for (sess <- S.session) sess.sendCometActorMessage(
      "BrowserDetails", Full(id), versionString
    )
    //code.comet.BrowserDetailsServer ! showingVersion
    <lift:comet type="BrowserDetails" name={id}>{xhtml}</lift:comet>
  }
}