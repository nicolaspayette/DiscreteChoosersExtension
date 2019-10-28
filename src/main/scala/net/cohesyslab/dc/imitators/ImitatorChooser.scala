package net.cohesyslab.dc.imitators

import io.github.carrknight
import net.cohesyslab.dc.Chooser
import net.cohesyslab.dc.WrappedChooser
import org.nlogo.api.AnonymousReporter
import org.nlogo.api.Context
import org.nlogo.api.Dump
import org.nlogo.api.ExtensionException
import org.nlogo.api.ScalaConversions._
import org.nlogo.core.LogoList

abstract class ImitatorChooser[C <: carrknight.Chooser[AnyRef, Double, Null]](
  override val delegate: C,
  observedChoosersReporter: AnonymousReporter,
) extends WrappedChooser(delegate) {

  override def choice(context: Context): AnyRef =
    delegate.updateAndChoose(null, getObservedChoosers(context).flatMap(_.lastObservation): _*)
  def getObservedChoosers(context: Context): Seq[Chooser] =
    extractChoosers(observedChoosersReporter.report(context, Array.empty[AnyRef]).toLogoObject)
  def extractChoosers(obj: AnyRef): Seq[Chooser] = obj match {
    case chooserObject: Chooser => Seq(chooserObject)
    case ll: LogoList => ll.flatMap(extractChoosers)
    case badValue => throw new ExtensionException(
      "The reporter used when constructing the chooser object must report either a single chooser object" +
        "or a list of chooser objects, but " + Dump.logoObject(badValue) + " was found in the result."
    )
  }

}
