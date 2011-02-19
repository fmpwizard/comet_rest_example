import sbt._

class LiftProject(info: ProjectInfo) extends DefaultWebProject(info) {


  //override def scanDirectories = Nil

  val liftVersion = "2.3-SNAPSHOT"
  

  val scalaToolsSnapshots = "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots" 

  override def compileOptions = super.compileOptions ++ Seq(Unchecked)
  override def testClasspath  = super.testClasspath +++ ("src" / "main" / "webapp")

  override def libraryDependencies = Set(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-widgets" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-mapper" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-json" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-actor" % liftVersion % "compile->default",
    "com.h2database" % "h2" % "1.2.138",
    "org.mortbay.jetty" % "jetty" % "6.1.22" % "test->default",
    "ch.qos.logback" % "logback-classic" % "0.9.26" % "compile->default",
    "org.slf4j" % "jcl-over-slf4j" % "1.6.1" % "compile->default"
  ) ++ super.libraryDependencies


}
