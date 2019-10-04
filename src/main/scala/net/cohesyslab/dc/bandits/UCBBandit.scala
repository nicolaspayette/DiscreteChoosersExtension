package net.cohesyslab.dc.bandits

import java.util.SplittableRandom

import io.github.carrknight.bandits.UCBBanditAlgorithm
import net.cohesyslab.dc.ChooserObject
import net.cohesyslab.dc.RichArgument
import net.cohesyslab.dc.SimpleRewardFunction
import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.Reporter
import org.nlogo.core.Syntax
import org.nlogo.core.Syntax.AgentsetType
import org.nlogo.core.Syntax.ListType
import org.nlogo.core.Syntax.WildcardType
import org.nlogo.core.Syntax.reporterSyntax

object UCBBanditBanditPrim extends Reporter {

  // TODO: make get/set primitives for all parameters

  val DefaultMinimumRewardExpected = 0.0
  val DefaultMaximumRewardExpected = 1.0
  val DefaultSigma = 1.0

  override def getSyntax: Syntax = reporterSyntax(
    right = List(ListType | AgentsetType), // the choices
    ret = WildcardType
  )

  override def report(args: Array[Argument], context: Context): AnyRef =
    new ChooserObject(
      new UCBBanditAlgorithm(
        SimpleRewardFunction,
        args(0).getOptionsArray(context.getRNG),
        0,
        new SplittableRandom(context.getRNG.nextLong()),
        DefaultMinimumRewardExpected,
        DefaultMaximumRewardExpected,
        DefaultSigma
      )
    )

}
