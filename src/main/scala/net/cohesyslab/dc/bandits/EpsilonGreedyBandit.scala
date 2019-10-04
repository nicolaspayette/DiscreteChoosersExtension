package net.cohesyslab.dc.bandits

import io.github.carrknight.bandits.EpsilonGreedyBandit
import net.cohesyslab.dc.ChooserObject
import net.cohesyslab.dc.RichArgument
import net.cohesyslab.dc.SimpleRewardFunction
import org.nlogo.api.Argument
import org.nlogo.api.Command
import org.nlogo.api.Context
import org.nlogo.api.Reporter
import org.nlogo.api.ScalaConversions._
import org.nlogo.core.Syntax
import org.nlogo.core.Syntax.AgentsetType
import org.nlogo.core.Syntax.ListType
import org.nlogo.core.Syntax.NumberType
import org.nlogo.core.Syntax.WildcardType
import org.nlogo.core.Syntax.commandSyntax
import org.nlogo.core.Syntax.reporterSyntax

object EpsilonGreedyBanditPrim extends Reporter {

  val DefaultEpsilonValue = 0.2

  override def getSyntax: Syntax = reporterSyntax(
    right = List(ListType | AgentsetType), // the choices
    ret = WildcardType
  )

  override def report(args: Array[Argument], context: Context): AnyRef =
    new ChooserObject(
      new EpsilonGreedyBandit(
        SimpleRewardFunction,
        args(0).getOptionsArray(context.getRNG),
        context.getRNG.nextLong(), // random seed
        DefaultEpsilonValue // default value for epsilon
      )
    )

}

object GetEpsilonPrim extends Reporter {
  override def getSyntax: Syntax = reporterSyntax(right = List(WildcardType), ret = NumberType)

  override def report(args: Array[Argument], context: Context): AnyRef =
    args(0).getChooserAs[EpsilonGreedyBandit[_, _, _]].getEpsilon.toLogoObject
}

object SetEpsilonPrim extends Command {
  override def getSyntax: Syntax = commandSyntax(List(WildcardType, NumberType))

  override def perform(args: Array[Argument], context: Context): Unit =
    args(0).getChooserAs[EpsilonGreedyBandit[_, _, _]].setEpsilon(args(1).getDoubleValue)
}