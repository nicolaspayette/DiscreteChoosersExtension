package net.cohesyslab.dc.bandits

import io.github.carrknight.bandits.EpsilonGreedyBandit
import net.cohesyslab.dc.bandits.EpsilonGreedyChooser.DefaultEpsilonValue
import net.cohesyslab.dc.utils.IdentityRewardFunction
import net.cohesyslab.dc.utils.RichArgument
import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.Reporter
import org.nlogo.core.Syntax
import org.nlogo.core.Syntax.AgentsetType
import org.nlogo.core.Syntax.ListType
import org.nlogo.core.Syntax.WildcardType
import org.nlogo.core.Syntax.reporterSyntax

object EpsilonGreedyChooserPrim extends Reporter {

  override def getSyntax: Syntax = reporterSyntax(
    right = List(ListType | AgentsetType), // the choices
    ret = WildcardType
  )

  override def report(args: Array[Argument], context: Context): AnyRef =
    new EpsilonGreedyChooser(
      args(0).getOptionsArray(context.getRNG),
      context.getRNG.nextLong()
    )
}

object EpsilonGreedyChooser {
  val DefaultEpsilonValue = 0.2
}

class EpsilonGreedyChooser(
  options: Array[AnyRef],
  randomSeed: Long
) extends AbstractBanditChooser(
  new EpsilonGreedyBandit(
    IdentityRewardFunction,
    options,
    randomSeed,
    DefaultEpsilonValue // default value for epsilon
  )
)