package net.cohesyslab.dc

import java.util.SplittableRandom

import io.github.carrknight.bandits.SoftmaxBanditAlgorithm
import org.nlogo.api.Argument
import org.nlogo.api.Command
import org.nlogo.api.Context
import org.nlogo.api.Reporter
import org.nlogo.api.ScalaConversions.RichAny
import org.nlogo.core.Syntax
import org.nlogo.core.Syntax.AgentsetType
import org.nlogo.core.Syntax.ListType
import org.nlogo.core.Syntax.NumberType
import org.nlogo.core.Syntax.WildcardType
import org.nlogo.core.Syntax.commandSyntax
import org.nlogo.core.Syntax.reporterSyntax

object SoftmaxBanditPrim extends Reporter {

  override def getSyntax: Syntax = reporterSyntax(
    right = List(
      ListType | AgentsetType // the choices
    ),
    ret = WildcardType
  )

  override def report(args: Array[Argument], context: Context): AnyRef =
    new ChooserObject(
      new SoftmaxBanditAlgorithm(
        SimpleRewardFunction,
        args(0).getOptionsArray,
        Double.MaxValue,
        new SplittableRandom(context.getRNG.nextLong()),
        1,
        x => x
      )
    )
}

object GetTemperaturePrim extends Reporter {
  override def getSyntax: Syntax = reporterSyntax(right = List(WildcardType), ret = NumberType)

  override def report(args: Array[Argument], context: Context): AnyRef =
    args(0).getChooserAs[SoftmaxBanditAlgorithm[_, _, _]].getTemperature.toLogoObject
}

object SetTemperaturePrim extends Command {
  override def getSyntax: Syntax = commandSyntax(List(WildcardType, NumberType))

  override def perform(args: Array[Argument], context: Context): Unit =
    args(0).getChooserAs[SoftmaxBanditAlgorithm[_, _, _]].setTemperature(args(1).getDoubleValue)
}