package net.cohesyslab.dc.bandits

import java.util.SplittableRandom

import io.github.carrknight.bandits.UCBBanditAlgorithm
import net.cohesyslab.dc.bandits.Ucb1Chooser.DefaultSigma
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

  override def getSyntax: Syntax = reporterSyntax(
    right = List(ListType | AgentsetType), // the choices
    ret = WildcardType
  )
  override def report(args: Array[Argument], context: Context): AnyRef =
    new Ucb1Chooser(
      args(0).getOptionsArray(context.getRNG),
      context.getRNG.nextLong()
    )
}

object Ucb1Chooser {
  val DefaultSigma = 1.0
}

class Ucb1Chooser(
  options: Array[AnyRef],
  randomSeed: Long
) extends AbstractBanditChooser(
  new UCBBanditAlgorithm(
    IdentityRewardFunction,
    options,
    0,
    new SplittableRandom(randomSeed),
    0.0,
    1.0,
    DefaultSigma
  )
) {
  override val observedResultValidationRule: ValidationRule[Double] = InRange(0, 1)
}

object SigmaPrim extends Getter[Ucb1Chooser] {
  override def get(chooser: Ucb1Chooser): Any = chooser.delegate.getSigma
}

object SetSigmaPrim extends DoubleSetter[Ucb1Chooser] {
  override def set(chooser: Ucb1Chooser, value: Double): Unit = chooser.delegate.setSigma(value)
}