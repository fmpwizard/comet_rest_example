package code
package model

import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.common._

/**
 * The singleton that has methods for accessing the database
 */



/*

for MAIN in 2.2.0. 2.3.0. 2.4.0. ; do
  for x in {1002..1090} ; do
      echo 'insert into qadashboard.browsertests(id, test_status, test_name, version_servicemanager, platform_name, browser_name )
            values
(null, 1, "Control render", "'${MAIN}${x}'", "Windows XP",  "IE7"  ) ,
(null, 1, "Control Render", "'${MAIN}${x}'", "Windows XP", "FF 3.0"),
(null, 1, "Control Render", "'${MAIN}${x}'", "Windows XP", "Safari 3.2"),
(null, 1, "Control Render", "'${MAIN}${x}'", "Windows Vista", "Safari 4"),
(null, 1, "Control Render", "'${MAIN}${x}'", "Windows Vista", "IE8"),
(null, 1, "Control Render", "'${MAIN}${x}'", "Mac OS X", "FF 3.6"),
(null, 1, "Control Render", "'${MAIN}${x}'", "Mac OS X", "Safari 5"),
(null, 1, "Control Render", "'${MAIN}${x}'", "Mac OS X", "Chrome 8"),
(null, 1, "Control Render", "'${MAIN}${x}'", "Mac OS X", "Opera 10");
 ' >> browsertests.full.sql;
  done;
done;



for MAIN in 2.2.0. 2.3.0. 2.4.0. ; do
  for x in {1002..1090} ; do
      echo 'insert into qadashboard.browsertests(id, test_status, test_name, version_servicemanager, platform_name, browser_name )
            values
(null, 1, "Graphs render", "'${MAIN}${x}'", "Windows XP",  "IE7"  ) ,
(null, 1, "Graphs Render", "'${MAIN}${x}'", "Windows XP", "FF 3.0"),
(null, 1, "Graphs Render", "'${MAIN}${x}'", "Windows XP", "Safari 3.2"),
(null, 1, "Graphs  Render", "'${MAIN}${x}'", "Windows Vista", "Safari 4"),
(null, 1, "Graphs Render", "'${MAIN}${x}'", "Windows Vista", "IE8"),
(null, 1, "Graphs Render", "'${MAIN}${x}'", "Mac OS X", "FF 3.6"),
(null, 1, "Graphs Render", "'${MAIN}${x}'", "Mac OS X", "Safari 5"),
(null, 1, "Graphs Render", "'${MAIN}${x}'", "Mac OS X", "Chrome 8"),
(null, 1, "Graphs Render", "'${MAIN}${x}'", "Mac OS X", "Opera 10");
 ' >> browsertests.full.sql;
  done;
done;















for MAIN in 2.2.0. 2.3.0. 2.4.0. ; do
  for x in {1002..1090} ; do
      echo 'insert into qadashboard.browsertests(id, test_status, test_name, version_servicemanager, platform_name, browser_name )
            values
(null, 1, "CG Works", "'${MAIN}${x}'", "Windows XP",  "IE7"  ) ,
(null, 1, "CG Works", "'${MAIN}${x}'", "Windows XP", "FF 3.0"),
(null, 1, "CG Works", "'${MAIN}${x}'", "Windows XP", "Safari 3.2"),
(null, 1, "CG Works", "'${MAIN}${x}'", "Windows Vista", "Safari 4"),
(null, 1, "CG Works", "'${MAIN}${x}'", "Windows Vista", "IE8"),
(null, 1, "CG Works", "'${MAIN}${x}'", "Mac OS X", "FF 3.6"),
(null, 1, "CG Works", "'${MAIN}${x}'", "Mac OS X", "Safari 5"),
(null, 1, "CG Works", "'${MAIN}${x}'", "Mac OS X", "Chrome 8"),
(null, 1, "CG Works", "'${MAIN}${x}'", "Mac OS X", "Opera 10");
 ' >> browsertests.full.sql;
  done;
done;

for MAIN in 2.2.0. 2.3.0. 2.4.0. ; do
  for x in {1002..1090} ; do
      echo 'insert into qadashboard.browsertests(id, test_status, test_name, version_servicemanager, platform_name, browser_name )
            values
(null, 1, "No Errors", "'${MAIN}${x}'", "Windows XP",  "IE7"  ) ,
(null, 1, "No Errors", "'${MAIN}${x}'", "Windows XP", "FF 3.0"),
(null, 1, "No Errors", "'${MAIN}${x}'", "Windows XP", "Safari 3.2"),
(null, 1, "No Errors", "'${MAIN}${x}'", "Windows Vista", "Safari 4"),
(null, 1, "No Errors", "'${MAIN}${x}'", "Windows Vista", "IE8"),
(null, 1, "No Errors", "'${MAIN}${x}'", "Mac OS X", "FF 3.6"),
(null, 1, "No Errors", "'${MAIN}${x}'", "Mac OS X", "Safari 5"),
(null, 1, "No Errors", "'${MAIN}${x}'", "Mac OS X", "Chrome 8"),
(null, 1, "No Errors", "'${MAIN}${x}'", "Mac OS X", "Opera 10");
 ' >> browsertests.full.sql;
  done;
done;



*/




object BrowserTests extends BrowserTests with LongKeyedMetaMapper[BrowserTests] with Logger{

  override def dbTableName = "browsertests" // define the DB table name
  def getBrowserTestResultList(version: String): List[(String, String)]=
      BrowserTests.findAllFields(Seq[SelectableField] (BrowserTests.test_name, BrowserTests.test_status),
          OrderBy(BrowserTests.id , Descending),
          Like(BrowserTests.version_servicemanager, version)
      ).map{ row => ( row.test_name.is, translateTestStatus(row.test_status.is) ) }

  def translateTestStatus(status: Boolean): String = {
    status match  {
      case true => "PASS"
      case false => "FAIL"
      case _ => "FAIL"
    }
  }

  def getBrowserTestResultByBrowserName(version: String, test_name: String): Map[String, (String, String)]= {
    BrowserTests.findAllFields(Seq[SelectableField] (
          BrowserTests.platform_name, BrowserTests.browser_name, BrowserTests.test_status),
          OrderBy(BrowserTests.test_status , Descending),
          Like(BrowserTests.version_servicemanager, version),
          Like(BrowserTests.test_name, test_name)
          ).map{
            row => (row.browser_name.is, (row.platform_name.is, translateTestStatus(row.test_status.is) ) )
          }.toMap
  }

  //This gets called form the rest api

  def updateOrAddBrowserTestResult(parsedBrowserTestResult : Box[api.RestHelperAPI.BrowserTestResultExtractor]) ={

    _finfBrowserTestResult(parsedBrowserTestResult) match {
      case 0 => addBrowserTestResult(parsedBrowserTestResult)
      case 1 => updateBrowserTestResult(parsedBrowserTestResult)
      case x => if(x > 1) info("We found more than one row");
    }
  }

  //Get count of rows that match the search
  private def _finfBrowserTestResult(parsedBrowserTestResult : Box[api.RestHelperAPI.BrowserTestResultExtractor]): Long ={

    //using open_! because there is no way to get here with an empty box
    val openedBrowserObj= parsedBrowserTestResult.open_!
    val cnt= BrowserTests.count(
          Like(
            BrowserTests.version_servicemanager, openedBrowserObj.service_manager_version.getOrElse("")
          ),
          Like(
            BrowserTests.test_name, openedBrowserObj.test_name.getOrElse("")
          ),
          Like(
            BrowserTests.browser_name, openedBrowserObj.browser_name.getOrElse("")
          )
    )
    debug("I found %d row(s)".format(cnt))

    cnt

  }


  def updateBrowserTestResult(parsedBrowserTestResult : Box[api.RestHelperAPI.BrowserTestResultExtractor]) = {
    //using open_! because there is no way to get here with an empty box
    val openedBrowserObj= parsedBrowserTestResult.open_!

    BrowserTests.findMap(
      Like(
        BrowserTests.version_servicemanager, openedBrowserObj.service_manager_version.getOrElse("")
      ),
      Like(
        BrowserTests.test_name, openedBrowserObj.test_name.getOrElse("")
      ),
      Like(
        BrowserTests.browser_name, openedBrowserObj.browser_name.getOrElse("")
      )
    ) {
      row =>
      row.test_status(openedBrowserObj.test_result.getOrElse(0)).save
      Empty
    }

    val row= BrowserTests.find(
          Like(
            BrowserTests.version_servicemanager, openedBrowserObj.service_manager_version.getOrElse("")
          ),
          Like(
            BrowserTests.test_name, openedBrowserObj.test_name.getOrElse("")
          ),
          Like(
            BrowserTests.browser_name, openedBrowserObj.browser_name.getOrElse("")
          )
    )
    debug("Updating row: %s".format(row))
  }


  def addBrowserTestResult(parsedBrowserTestResult : Box[api.RestHelperAPI.BrowserTestResultExtractor]) = {

    _finfBrowserTestResult(parsedBrowserTestResult)
    //using open_! because there is no way to get here with an empty box
    val openedBrowserObj= parsedBrowserTestResult.open_!
    val browserTestResultRecord: BrowserTests = BrowserTests.create
    //using open_! because there is no way to get here with an empty box
    browserTestResultRecord.version_servicemanager(
      openedBrowserObj.service_manager_version.getOrElse("")
    )
    browserTestResultRecord.test_name(
      openedBrowserObj.test_name.getOrElse("")
    )
    browserTestResultRecord.test_status(
      openedBrowserObj.test_result.getOrElse("")
    )
    browserTestResultRecord.browser_name (
      openedBrowserObj.browser_name.getOrElse("")
    )
    browserTestResultRecord.platform_name (
      openedBrowserObj.platform_name.getOrElse("")
    )

    debug("Saving %s".format(parsedBrowserTestResult))
    val saved: Boolean = browserTestResultRecord.save
  }

  /**
   * BrowserTests.test_status is boolean, but from the json
   * we get an Int, so this implicit functions makes the conversion
   */

  implicit def int2Boolean(a: Any): Boolean= {
    a match {
      case 1 => true
      case 0 => false
    }
  }

}

class BrowserTests extends LongKeyedMapper[BrowserTests] {
  def getSingleton = BrowserTests
  
  def primaryKeyField = id
  object id extends MappedLongIndex(this)

  object version_servicemanager extends MappedString(this, 15) {
    override def dbIndexed_? = true
  }
  object test_name extends MappedString(this, 250) {
    override def dbIndexed_? = true
  }
  object test_status extends MappedBoolean(this) {
    override def dbIndexed_? = true
  }
  object platform_name extends MappedString(this, 200) {
    override def dbIndexed_? = true
  }
  object browser_name extends MappedString(this, 200) {
    override def dbIndexed_? = true
  }  

  
}
