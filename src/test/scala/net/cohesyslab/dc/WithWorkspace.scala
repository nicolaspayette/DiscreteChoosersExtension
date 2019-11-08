package net.cohesyslab.dc

import org.nlogo.headless.HeadlessWorkspace
import org.scalatest.FunSuite

trait WithWorkspace {
  def withWorkspace[R](modelString: String = "", worldSize: Int = 1)(f: HeadlessWorkspace => R): R = {
    implicit val workspace: HeadlessWorkspace = HeadlessWorkspace.newInstance
    try {
      workspace.initForTesting(worldSize, s"extensions [ dc ]\n$modelString")
      f(workspace)
    } finally {
      workspace.dispose()
    }
  }
  def command(source: String)(implicit workspace: HeadlessWorkspace): Unit = workspace.command(source)
  def report(source: String)(implicit workspace: HeadlessWorkspace): AnyRef = workspace.report(source)
}

class WithWorkspaceSuite extends FunSuite with WithWorkspace {

  test("dc extension loaded") {
    withWorkspace() {
      _.report("dc:about")
    }
  }

  test("can add global") {
    withWorkspace("globals [ x ]") { implicit ws =>
      command("set x 10")
      assert(report("x") === 10)
    }
  }

  test("can set world size") {
    withWorkspace(worldSize = 5) { implicit ws =>
      assert(report("world-width") === 11)
    }
  }

}
