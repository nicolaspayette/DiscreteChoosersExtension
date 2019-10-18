package net.cohesyslab.dc.bandits

import java.util.SplittableRandom

import io.github.carrknight.bandits.UCBBanditAlgorithm
import net.cohesyslab.dc.ChooserObject
import net.cohesyslab.dc.utils.DoubleSetter
import net.cohesyslab.dc.utils.Getter
import net.cohesyslab.dc.utils.IdentityRewardFunction
import net.cohesyslab.dc.utils.InRange
import net.cohesyslab.dc.utils.RichArgument
import net.cohesyslab.dc.utils.ValidationRule
import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.Reporter
import org.nlogo.core.Syntax
import org.nlogo.core.Syntax.AgentsetType
import org.nlogo.core.Syntax.ListType
import org.nlogo.core.Syntax.WildcardType
import org.nlogo.core.Syntax.reporterSyntax

object Ucb1ChooserPrim extends Reporter {

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
      IdentityRewardFunction,
      options,
      0,
      rng,
      0.0,
      1.0,
      DefaultSigma
    )
  ) {
    override val observedResultValidationRule: ValidationRule[Double] = InRange(0, 1)
  }

}

object SigmaPrim extends Getter[UCBBanditAlgorithm[_, _, _]] {
  override def get(chooser: UCBBanditAlgorithm[_, _, _]): Any = chooser.getSigma
}

object SetSigmaPrim extends DoubleSetter[UCBBanditAlgorithm[_, _, _]] {
  override def set(chooser: UCBBanditAlgorithm[_, _, _], value: Double): Unit = chooser.setSigma(value)
}