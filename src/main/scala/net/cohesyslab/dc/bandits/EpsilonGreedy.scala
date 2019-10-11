package net.cohesyslab.dc.bandits

import io.github.carrknight.bandits.EpsilonGreedyBandit
import net.cohesyslab.dc.ChooserObject
import net.cohesyslab.dc.IdentityRewardFunction
import net.cohesyslab.dc.NumberGetter
import net.cohesyslab.dc.NumberSetter
import net.cohesyslab.dc.RichArgument
import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.Reporter
import org.nlogo.core.Syntax
import org.nlogo.core.Syntax.AgentsetType
import org.nlogo.core.Syntax.ListType
import org.nlogo.core.Syntax.WildcardType
import org.nlogo.core.Syntax.reporterSyntax

object EpsilonGreedyChooserPrim extends Reporter {

  val DefaultEpsilonValue = 0.2

  override def getSyntax: Syntax = reporterSyntax(
    right = List(ListType | AgentsetType), // the choices
    ret = WildcardType
  )

  override def report(args: Array[Argument], context: Context): AnyRef =
    new ChooserObject(
      new EpsilonGreedyBandit(
        IdentityRewardFunction,
        args(0).getOptionsArray(context.getRNG),
        context.getRNG.nextLong(), // random seed
        DefaultEpsilonValue // default value for epsilon
      )
    )

}

object EpsilonPrim extends NumberGetter[EpsilonGreedyBandit[_, _, _]](_.getEpsilon)

object SetEpsilonPrim extends NumberSetter[EpsilonGreedyBandit[_, _, _]](_.setEpsilon(_))