package net.cohesyslab.dc.bandits

import java.util.SplittableRandom

import io.github.carrknight.bandits.UCBBanditAlgorithm
import net.cohesyslab.dc.ChooserObject
import net.cohesyslab.dc.RichAnyRef
import net.cohesyslab.dc.RichArgument
import net.cohesyslab.dc.RichDouble
import net.cohesyslab.dc.SimpleRewardFunction
import net.cohesyslab.dc.check
import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.Reporter
import org.nlogo.core.Syntax
import org.nlogo.core.Syntax.AgentsetType
import org.nlogo.core.Syntax.ListType
import org.nlogo.core.Syntax.WildcardType
import org.nlogo.core.Syntax.reporterSyntax

object UCBBanditBanditPrim extends Reporter {

  // TODO: make get/set primitives for DefaultSigma
  // TODO: parametrise everything so that rewards are Double

  val DefaultSigma = 1.0
  override def getSyntax: Syntax = reporterSyntax(
    right = List(ListType | AgentsetType), // the choices
    ret = WildcardType
  )
  override def report(args: Array[Argument], context: Context): AnyRef =
    new UCBBanditChooserObject(
      args(0).getOptionsArray(context.getRNG),
      new SplittableRandom(context.getRNG.nextLong())
    )

  class UCBBanditChooserObject(options: Array[AnyRef], rng: SplittableRandom) extends ChooserObject(
    new UCBBanditAlgorithm(
      SimpleRewardFunction,
      options,
      0,
      rng,
      0.0,
      1.0,
      DefaultSigma
    )
  ) {
    override def observe(choiceMade: AnyRef, resultObserved: AnyRef): Unit = {
      check(
        resultObserved.as[Double].inRange(0, 1),
        "The result observed by a UCB Bandit must be between 0 and 1, inclusively."
      )
      super.observe(choiceMade, resultObserved)
    }
  }

}
