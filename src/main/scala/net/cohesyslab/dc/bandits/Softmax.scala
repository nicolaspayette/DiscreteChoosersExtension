package net.cohesyslab.dc.bandits

import java.util.SplittableRandom

import io.github.carrknight.bandits.SoftmaxBanditAlgorithm
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

object TemperaturePrim extends NumberGetter[SoftmaxBanditAlgorithm[_, _, _]](_.getTemperature)

object SetTemperaturePrim extends NumberSetter[SoftmaxBanditAlgorithm[_, _, _]](_.setTemperature(_))