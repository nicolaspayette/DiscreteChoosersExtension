package net.cohesyslab.dc.bandits

import java.util.SplittableRandom

import io.github.carrknight.bandits.SoftmaxBanditAlgorithm
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

object SoftmaxChooserPrim extends Reporter {

  val DefaultTemperatureValue = 1.0

  override def getSyntax: Syntax = reporterSyntax(
    right = List(ListType | AgentsetType), // the choices
    ret = WildcardType
  )

  override def report(args: Array[Argument], context: Context): AnyRef =
    new ChooserObject(
      new SoftmaxBanditAlgorithm(
        IdentityRewardFunction,
        args(0).getOptionsArray(context.getRNG),
        Double.MaxValue,
        new SplittableRandom(context.getRNG.nextLong()),
        DefaultTemperatureValue,
        x => x
      )
    )
}

object TemperaturePrim extends Getter[SoftmaxBanditAlgorithm[_, _, _]] {
  override def get(chooser: SoftmaxBanditAlgorithm[_, _, _]): Any = chooser.getTemperature
}

object SetTemperaturePrim extends DoubleSetter[SoftmaxBanditAlgorithm[_, _, _]] {
  override def set(chooser: SoftmaxBanditAlgorithm[_, _, _], value: Double): Unit = chooser.setTemperature(value)
}