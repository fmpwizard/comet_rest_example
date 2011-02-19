package code {
package snippet {

import code.model.{AutomatedTests}
import code.snippet.Param._

import _root_.scala.xml.{NodeSeq, Text}

import _root_.net.liftweb._
import util._
import common.Logger
import mapper.{OrderBy, Descending, SelectableField}
import http.SHtml._
import http.S._
import http.js.JsCmds.{SetHtml, SetValueAndFocus}
import Helpers._

class Overview extends Logger {

  /**
    * Generate the Test Result view section
    */

  val showingVersion= overviewVersionString openOr("N/A")
  debug(showingVersion)


  def renderAgentResult( xhtml: NodeSeq ) = {
    val testResultList = AutomatedTests.getAgentTestResultList( showingVersion )
    // for this next line see 
    // http://stackoverflow.com/questions/4446949/scala-get-unique-values-from-list-with-a-twist
    val testResultFiltered= testResultList.groupBy(_._1).map(
                              pair => (pair._1, pair._2.reduceLeft((a,b) => 
                                if ("FAIL" == a._2 || "FAIL" == b._2) 
                                (a._1, "FAIL") else a))
                            ).map(_._2).toList.sorted

    def bindTestResults(template: NodeSeq): NodeSeq = {
      testResultFiltered.flatMap{ case (testName, testResult) => bind(
        "test", template,
        FuncAttrBindParam(
            "classname", { 
              ns : NodeSeq => Text(if (testResult == "FAIL") "error" else "success" )
            }, "class"
          ),
        "name" -> link("/agent-details/" + showingVersion, () => {}
                    , <span>{testName}</span>),
        "result" -> link("/agent-details/" + showingVersion, () => {}
                    ,  <span>{testResult}</span>)
        )
      }
    }
    bind("tests",xhtml, "version" -> showingVersion, "testResultListRow" -> bindTestResults _)
  }
  

  def renderServiceManagerResult( xhtml: NodeSeq ) = {
    val testResultList = AutomatedTests.getServiceManagerTestResultList( showingVersion )
    // for this next line see
    // http://stackoverflow.com/questions/4446949/scala-get-unique-values-from-list-with-a-twist
    val testResultFiltered= testResultList.groupBy(_._1).map(pair => (pair._1, pair._2.reduceLeft((a,b) => if ("FAIL" == a._2 || "FAIL" == b._2) (a._1, "FAIL") else a))).map(_._2).toList.sorted

    def bindTestResults(template: NodeSeq): NodeSeq = {
      testResultFiltered.flatMap{ case (testName, testResult) => bind(
          "test", template, 
          FuncAttrBindParam(
            "classname", { 
              ns : NodeSeq => Text(if (testResult == "FAIL") "error" else "success" )
            }, "class"
          ),
        "name" -> link("/servicemanager-details/" + showingVersion, () => {}
                    , <span>{testName}</span>),
        "result" -> link("/servicemanager-details/" + showingVersion, () => {}
                    ,  <span>{testResult}</span>)
        )
      }
    }
    bind("tests",xhtml, "version" -> showingVersion, "testResultListRow" -> bindTestResults _)
  }


}

}
}
