package net.cohesyslab.dc.imitators

import io.github.carrknight.Chooser
import net.cohesyslab.dc.ChooserObject
import org.nlogo.api.AnonymousReporter
import org.nlogo.api.Context
import org.nlogo.api.Dump
import org.nlogo.api.ExtensionException
import org.nlogo.api.ScalaConversions._
import org.nlogo.core.LogoList

class ImitatorChooserObject(
  override val chooser: Chooser[AnyRef, Double, Null],
  observedChoosersReporter: AnonymousReporter,
) extends ChooserObject(chooser) {

  override def choice(context: Context): AnyRef =
    chooser.updateAndChoose(null, getObservedChoosers(context).flatMap(_.lastObservation): _*)
  def getObservedChoosers(context: Context): Seq[ChooserObject] =
    extractChoosers(observedChoosersReporter.report(context, Array.empty[AnyRef]).toLogoObject)
  def extractChoosers(obj: AnyRef): Seq[ChooserObject] = obj match {
    case chooserObject: ChooserObject => Seq(chooserObject)
    case ll: LogoList => ll.flatMap(extractChoosers)
    case badValue => throw new ExtensionException(
      "The reporter used when constructing the chooser object must report either a single chooser object" +
        "or a list of chooser objects, but " + Dump.logoObject(badValue) + " was found in the result."
    )
  }

}
