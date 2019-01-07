package net.cohesyslab.dc

import io.github.carrknight.bandits.EpsilonGreedyBandit
import org.nlogo.api.AgentSet
import org.nlogo.api.Argument
import org.nlogo.api.Command
import org.nlogo.api.Context
import org.nlogo.api.Reporter
import org.nlogo.api.ScalaConversions.RichAny
import org.nlogo.core.LogoList
import org.nlogo.core.Syntax
import org.nlogo.core.Syntax.WildcardType
import org.nlogo.core.Syntax.commandSyntax
import org.nlogo.core.Syntax.NumberType
import org.nlogo.core.Syntax.reporterSyntax
import org.nlogo.core.Syntax.AgentsetType
import org.nlogo.core.Syntax.ListType
import org.nlogo.core.Syntax.RepeatableType
import org.nlogo.core.Syntax.ReporterType

object SimpleEpsilonGreedyBanditPrim extends Reporter {

  override def getSyntax: Syntax = reporterSyntax(
    right = List(NumberType, NumberType),
    ret = WildcardType
  )

  override def report(args: Array[Argument], context: Context): AnyRef = {
    val numberOfOptions = args(0).getIntValue
    val epsilon = args(1).getDoubleValue
    val randomSeed = context.getRNG.nextLong()
    val optionsAvailable = Array.range(0, numberOfOptions).map(_.toLogoObject)
    new ChooserObject(
      new EpsilonGreedyBandit(SimpleRewardFunction, optionsAvailable, randomSeed, epsilon)
    )
  }

}

object EpsilonGreedyBanditPrim extends Reporter {

  override def getSyntax: Syntax = reporterSyntax(
    right = List(ListType | AgentsetType, NumberType, ReporterType | RepeatableType),
    ret = WildcardType,
    defaultOption = Option(2),
    minimumOption = Option(2)
  )

  override def report(args: Array[Argument], context: Context): AnyRef = {
    val optionsAvailable: Array[AnyRef] = args(0).get match {
      case agentSet: AgentSet => agentSet.toArray
      case logoList: LogoList => logoList.toArray
    }
    val epsilon = args(1).getDoubleValue
    val randomSeed = context.getRNG.nextLong()
    // TODO: allow custom reward function
    new ChooserObject(
      new EpsilonGreedyBandit(SimpleRewardFunction, optionsAvailable, randomSeed, epsilon)
    )
  }

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