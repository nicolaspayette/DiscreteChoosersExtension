package net.cohesyslab.dc

import org.nlogo.api.DefaultClassManager
import org.nlogo.api.PrimitiveManager
import org.nlogo.core.Primitive

class ExtensionClassManager(primitives: Primitive*) extends DefaultClassManager {

  override def load(primManager: PrimitiveManager): Unit =
    primitives.foreach(prim => primManager.addPrimitive(makePrimName(prim), prim))

  private def makePrimName(obj: Any): String =
    obj.getClass.getSimpleName
      .split(raw"(?=\p{Upper})")
      .map(_.toLowerCase)
      .filterNot(_ == "prim$")
      .mkString("-")
}