package code
package comet

import code.api.CellToUpdate
import code.lib.GridRenderHelper._
import code.model.{BrowserTests}
import code.snippet.Param._

import scala.xml.{NodeSeq, Text, Elem}
import scala.actors.Actor
//import scala.actors.Actor._


import net.liftweb._
import util._
import actor._
import http._
import common.{Box, Full,Logger}
import mapper.{OrderBy, Descending, SelectableField}
import http.SHtml._
import http.S._
import http.js.JsCmds.{SetHtml, SetValueAndFocus, Replace}
import net.liftweb.http.js.JE.Str
import Helpers._


/**
 * This is the message we pass around to
 * register each named comet actor with a dispatcher that
 * only updates the specific version it monitors
 */
case class registerCometActor(actor: CometActor, version: String)

class BrowserDetails extends CometActor with SimpleInjector with Logger {

  override def defaultPrefix = Full("comet")

  // time out the comet actor if it hasn't been on a page for 2 miniutes
  override def lifespan = Full(120 seconds)

  var showingVersion= ""

  val testResults= new Inject[Box[List[Map[String,(String, String)]]]](
    Full(List(Map("N/A"->("N/A", "N/A"))))
  ){}
  testResults.default.set(getTestResults)


  // Inject the method call, not just the val
  def getTestResults: Box[List[Map[String,(String, String)]]]= {
    val testResultControlRender= BrowserTests.getBrowserTestResultByBrowserName(
      showingVersion, "Control render" )
    val testResultGraphsRender= BrowserTests.getBrowserTestResultByBrowserName(
      showingVersion, "Graphs render" )
    val testResultCGWorks= BrowserTests.getBrowserTestResultByBrowserName(
      showingVersion, "CG Works" )
    val testResultNoErrors= BrowserTests.getBrowserTestResultByBrowserName(
      showingVersion, "No Errors" )

    // a list of all our tests
    val tests = Full(List(testResultControlRender, testResultGraphsRender,
      testResultCGWorks, testResultNoErrors))
    tests

  }

  /**
   * On page load, this method does a full page render
   */
  def render= {
    testResults.doWith(getTestResults){
      renderGrid(showingVersion, testResults.vend)
    }
  }

  /**
   * We can get two kinds of messages
   * 1- A CellToUpdate, which has info about which cell on the UI
   * we need to update. The rest api sends this message
   *
   * 2- A string which is the version the comet actor is displaying info about
   * On page load we get this message
   *
   */
  override def lowPriority: PartialFunction[Any,Unit] = {
    case CellToUpdate(index, rowName, version, cssClass, cellNotes) => {
      info("Comet Actor %s will do a partial update".format(this))
      info("[API]: Updating BrowserTestResults for version: %s".format(version))
      showingVersion = version

      /**
       * each td in the html grid has an id that is
       * [0-9] + browser name
       * I use this to uniquely identify which cell to update
       *
       */
      partialUpdate(
        Replace((index + rowName),
            <td id={(index + rowName)} class={cssClass}>{cellNotes}</td>
         )
      )
    }
    case version: String => {
      info("[URL]: Updating BrowserTestResults for version: %s".format(version))
      showingVersion= version

      /**
       * We get the DispatcherActor that sends message to all the
       * CometActors that are displaying a specific version number.
       * And we register ourself with the dispatcher
       */

      MyListeners.listenerFor(showingVersion) ! registerCometActor(this, version)
      info("Registering comet actor: %s".format(this))
      reRender()
    }
    case _ => info("Not sure how we got here.")
  }


}

/**
 * This class keeps a list of comet actors that need to update the UI
 * if we get new data through the rest api
 */
class DispatcherActor(version: String) extends LiftActor  with Logger{

  info("DispatcherActor got version: %s".format(version))
  private var cellToUpdate= CellToUpdate(0, "N/A", "N/A", "error", "None")
  private var cometActorsToUpdate: List[CometActor]= List()

  def createUpdate = cellToUpdate

  override def messageHandler  = {
    /**
     * if we do not have this actor in the list, add it (register it)
     */
    case registerCometActor(actor, version) =>
      if(cometActorsToUpdate.contains(actor) == false){
        info("We are adding actor: %s to the list".format(actor))
        cometActorsToUpdate= actor :: cometActorsToUpdate
      } else {
        info("The list so far is %s".format(cometActorsToUpdate))
      }

    /**
     * Go throuth the the list of actors and send them a cellToUpdate message
     */
    case CellToUpdate(index, rowName, version, cssClass, cellNotes) => {
      cellToUpdate = CellToUpdate(index, rowName, version, cssClass, cellNotes)
      info("We will update these comet actors: %s showing version: %s".format(
        cometActorsToUpdate, version))
      cometActorsToUpdate.foreach(_ ! cellToUpdate)
    }
    case _ => "Bye"
  }

}


/**
 * Keep a map of versions -> dispatchers, if no dispatcher is found, create one
 * comet actors get the ref to their dispatcher using this object,
 * so they can register themselves and the rest
 * api gets the dispatcher that is monitoring a specific version
 *
 */
object MyListeners extends Logger{
  //How about creating a ListenerManager (a separate Actor)
  //for each of the items you're going to have:


  private var listeners: Map[String, LiftActor] = Map()

  def listenerFor(str: String): LiftActor = synchronized {
    listeners.get(str) match {
      case Some(a) => info("Our map is %s".format(listeners)); a
      case None => {
        val ret = new DispatcherActor(str)
        listeners += str -> ret
        info("Our map is %s".format(listeners))
        ret
      }
    }
  }



  //So, you'll have a separate dispatcher for each of your URL parameters
  //and the CometActors can register with them and the REST thing can find
  //them to send the messages.



}
