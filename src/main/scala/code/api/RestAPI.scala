package code
package api


import scala.xml.{Elem, Node, NodeSeq, Text}

import net.liftweb.common.{Box,Empty,Failure,Full,Logger}
import net.liftweb.http.rest.{RestHelper}
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.json._
import net.liftweb.actor._

import model._
import code.comet.MyListeners._

import net.liftweb.http._
import net.liftweb.util.Helpers

case class CellToUpdate(colIndex: Int, rowName: String,
                        version: String, testResult: String, cellNotes: String)

object RestHelperAPI extends RestHelper with Logger {

  /**
   * This case class is use to easily parse the json text we get through the REST API
   */
  case class BrowserTestResultExtractor(
                              apiversion: Option[String],
                              service_manager_version: Option[String],
                              test_name: Option[String],
                              test_result: Option[Int],
                              platform_name: Option[String],
                              browser_name: Option[String],
                              test_notes: Option[String]
                                       )

  /**
   * The heart of the rest api, we listen for urls like:
   * http://hostname/v1/rest/browsertests/version-number
   */
  serve {
    case "v1" :: "rest" :: "browsertests" :: _ JsonPut json -> _ =>
      // json is a net.liftweb.json.JsonAST.JValue
      verifyBrowserTestResult(Full(json.extract[BrowserTestResultExtractor]))

  }

  /**
   * See if the json matches your requirements
   * If so, write to the DB and update the UI
   *
   */
  def verifyBrowserTestResult(parsedBrowserTestResult: Box[BrowserTestResultExtractor]): LiftResponse=
    parsedBrowserTestResult match {
    case Full(browserTestResult) => {
      browserTestResult match {
      /**
       * If we have all fields, add them to the database
       */

        case BrowserTestResultExtractor(
          Some(apiVersion),
          Some(srvmgrVersion),
          Some(testName),
          Some(testResult),
          Some(platform),
          Some(browser),
          Some(cellNotes)) => {
            debug("Parsing of json complete for: %s".format(browser))

          /**
           * updateOrAddBrowserTestResult will add a new row on the MySQL table
           * or update one that is already present
           * It will update for cases when we re-run a test.
           *
           */
            BrowserTests.updateOrAddBrowserTestResult(parsedBrowserTestResult)

          /**
           * Tell the BrowserDetails comet actor to update the UI
           */

            debug(
              "REST API will send an update to actor: %s: ".format(listenerFor(srvmgrVersion))
            )

          /**
           * listenerFor(srvmgrVersion) returns a DispatcherActor that in turn
           * will send the CellToUpdate clas class to the comet actors that are
           * displaying info about the version we got json data for
           */
            listenerFor(srvmgrVersion) match {
              case a: LiftActor => a ! CellToUpdate(
                testName, browser, srvmgrVersion, testResult, cellNotes
              )
              case _ => info("No actor to send an update")
            }
            debug("We will update column: %s, row: %s".format(testName, browser))

            NoContentResponse()
        }
        // Else. log an error and return a erro4 400 with a message
        case BrowserTestResultExtractor(a, b, c, d, e, f, g) => {
          info("It did not passed: %s".format(a))
          val msg= ("We are missing some fields, " +
            "we got: %s, %s, %s, %s, %s, %s").format( b, c, d, e, f, g)
          ResponseWithReason(BadResponse(), msg)
        }
      }
    }
    case Failure(msg, _, _) => {
      info(msg)
      ResponseWithReason(BadResponse(), msg)
    }
    case error => {
      info("Parsed browserTestResult as : " + error)
      BadResponse()
    }
  }

  /**
   * Implicits methods are caled by the compiler
   * before giving you an error that it expects type A but
   * got type B
   */

  /**
   * Handy method to convert a test name (string) into a column index on the
   * page (browser) (Int)
   */
  implicit def testNameToColIndex(testName: String): Int= {
    testName match{
      case "Control Render" => 0
      case "Graphs render" => 1
      case "CG Works" => 2
      case "No Errors" => 3
    }
  }

  /**
   * Handy method to convert a test result from json (Int) to
   * a css class nam to be applied on the UI
   */
  implicit def testResultToCSSClass(testResult: Int): String= {
    testResult match{
      case 0 => "error"
      case 1 => "success"
    }
  }

}
