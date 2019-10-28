package net.cohesyslab.dc.bandits

import java.util.SplittableRandom

import io.github.carrknight.bandits.SoftmaxBanditAlgorithm
import net.cohesyslab.dc.bandits.SoftmaxChooser.DefaultTemperatureValue
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

object SoftmaxChooserPrim extends Reporter {

  override def getSyntax: Syntax = reporterSyntax(
    right = List(ListType | AgentsetType), // the choices
    ret = WildcardType
  )

  override def report(args: Array[Argument], context: Context): AnyRef =
    new SoftmaxChooser(
      args(0).getOptionsArray(context.getRNG),
      context.getRNG.nextLong(),
    )
}

object SoftmaxChooser {
  val DefaultTemperatureValue = 1.0
}

class SoftmaxChooser(
  options: Array[AnyRef],
  randomSeed: Long
) extends AbstractBanditChooser(
  new SoftmaxBanditAlgorithm(
    IdentityRewardFunction,
    options,
    Double.MaxValue,
    new SplittableRandom(randomSeed),
    DefaultTemperatureValue,
    x => x
  )
)

object TemperaturePrim extends Getter[SoftmaxChooser] {
  override def get(chooser: SoftmaxChooser): Any = chooser.delegate.getTemperature
}

object SetTemperaturePrim extends DoubleSetter[SoftmaxChooser] {
  override def set(chooser: SoftmaxChooser, value: Double): Unit = chooser.delegate.setTemperature(value)
}