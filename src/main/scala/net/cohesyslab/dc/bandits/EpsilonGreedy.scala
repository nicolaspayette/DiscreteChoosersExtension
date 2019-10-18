package net.cohesyslab.dc.bandits

import io.github.carrknight.bandits.EpsilonGreedyBandit
import net.cohesyslab.dc.ChooserObject
import net.cohesyslab.dc.utils.DoubleSetter
import net.cohesyslab.dc.utils.Getter
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

object EpsilonPrim extends Getter[EpsilonGreedyBandit[_, _, _]] {
  override def get(chooser: EpsilonGreedyBandit[_, _, _]): Any = chooser.getEpsilon
}

object SetEpsilonPrim extends DoubleSetter[EpsilonGreedyBandit[_, _, _]] {
  override def set(chooser: EpsilonGreedyBandit[_, _, _], value: Double): Unit = chooser.setEpsilon(value)
}