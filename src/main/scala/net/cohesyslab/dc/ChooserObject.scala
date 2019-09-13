package net.cohesyslab.dc

import io.github.carrknight.Chooser
import io.github.carrknight.Observation
import org.nlogo.core.ExtensionObject

class ChooserObject(val chooser: Chooser[AnyRef, AnyRef, Null])
  extends Chooser[AnyRef, AnyRef, Null] with ExtensionObject {

  override def getExtensionName: String = "dc"
  override def getNLTypeName: String = chooser.getClass.getSimpleName
  override def recursivelyEqual(obj: AnyRef): Boolean = eq(obj)
  override def dump(readable: Boolean, exporting: Boolean, reference: Boolean): String =
    hashCode.toHexString

  override def getLastChoice: AnyRef = chooser.getLastChoice
  override def updateAndChoose(
    observation: Observation[AnyRef, AnyRef, Null],
    additionalObservations: Observation[AnyRef, AnyRef, Null]*): AnyRef =
    chooser.updateAndChoose(observation, additionalObservations: _*)
}
