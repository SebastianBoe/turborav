package TurboRav

import Chisel._

import scala.xml.PrettyPrinter
import org.apache.commons.io.FileUtils
import java.io.File

/** This class adds JUnit test report generation to the base Chisel
  * Tester for consumption by CI tools like Jenkins.
  *
  * The JUnit report is written to the overridable path getJunitPath()
  * that defaults to be jenkins.xml inside the target dir.
  */
class JUnitTester[+T <: Module](c: T, isTrace: Boolean = true) extends Tester(c, isTrace) {

  override def finish {
    generateXmlForJenkins()
    super.finish
  }

  private def generateXmlForJenkins() {
    val error_msg = "" //TODO: Find a better error message
    val file_contents = new PrettyPrinter(80, 2).format(
      <testsuite>
        <testcase classname={getTestName}>
        {if (ok) "" else <failure type="a_type">{error_msg}</failure>}
        </testcase>
      </testsuite>
    ) + "\n"

    FileUtils.writeStringToFile(
      new File(getJUnitPath),
      file_contents
    )
  }

  protected def getJUnitPath: String = {
    Driver.targetDir + "/jenkins.xml"
  }

  protected def getTestName: String = {
    c.name
  }
}
