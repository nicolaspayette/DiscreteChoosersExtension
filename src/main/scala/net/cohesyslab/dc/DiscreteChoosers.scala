package net.cohesyslab.dc

import io.github.carrknight.Observation
import org.nlogo.api.Argument
import org.nlogo.api.Command
import org.nlogo.api.Context
import org.nlogo.api.DefaultClassManager
import org.nlogo.api.PrimitiveManager
import org.nlogo.api.Reporter
import org.nlogo.api.ScalaConversions.RichAny
import org.nlogo.core.Syntax
import org.nlogo.core.Syntax.WildcardType
import org.nlogo.core.Syntax.commandSyntax
import org.nlogo.core.Syntax.reporterSyntax
import org.nlogo.core.Syntax.ReadableType

class DiscreteChoosersExtension extends DefaultClassManager {

  def load(primManager: PrimitiveManager): Unit = List(
    AddObservationPrim,
    UpdateChoicePrim,
    LastChoicePrim,
    EpsilonGreedyBanditPrim,
    GetEpsilonPrim,
    SetEpsilonPrim
  ).foreach(prim => primManager.addPrimitive(makePrimName(prim), prim))

  def makePrimName(obj: Any): String =
    obj.getClass.getSimpleName
      .split("(?=\\p{Upper})")
      .map(_.toLowerCase)
      .filterNot(_ == "prim$")
      .mkString("-")
}

object LastChoicePrim extends Reporter {

  override def getSyntax: Syntax = reporterSyntax(
    right = List(WildcardType),
    ret = WildcardType
  )

  override def report(args: Array[Argument], context: Context): AnyRef =
    args(0).get.as[ChooserObject].getLastChoice.toLogoObject
}

object AddObservationPrim extends Command {

  override def getSyntax: Syntax = commandSyntax(
    right = List(WildcardType | ReadableType),
    defaultOption = Option(3),
    minimumOption = Option(3)
  )

  override def perform(args: Array[Argument], context: Context): Unit = {
    val chooser = args(0).get.as[ChooserObject]
    val choiceMade = args(1).get
    val resultObserved = args(2).get
    val observationContext = args.lift(3).map(_.get).orNull
    chooser.updateAndChoose(new Observation(choiceMade, resultObserved, observationContext))
  }
}

object UpdateChoicePrim extends Command {
  override def getSyntax: Syntax = commandSyntax(List(WildcardType))
  override def perform(args: Array[Argument], context: Context): Unit =
    args(0).get.as[ChooserObject].updateAndChoose(null)
}