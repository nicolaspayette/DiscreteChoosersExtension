package net.cohesyslab.dc.imitators

import java.util.SplittableRandom

import io.github.carrknight.Observation
import io.github.carrknight.heatmaps.regression.FeatureExtractor
import io.github.carrknight.imitators.MonoBelief
import io.github.carrknight.imitators.ParticleSwarm
import net.cohesyslab.dc.utils.IdentityRewardFunction
import net.cohesyslab.dc.utils.RichArgument
import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.Patch
import org.nlogo.api.Reporter
import org.nlogo.core.Syntax
import org.nlogo.core.Syntax.PatchsetType
import org.nlogo.core.Syntax.ReporterType
import org.nlogo.core.Syntax.WildcardType
import org.nlogo.core.Syntax.reporterSyntax

object ParticleSwarmChooserPrim extends Reporter {

  val DefaultWeightOfImitation = 1.0
  val DefaultWeightOfMemory = 1.0
  val DefaultInertia = 0.7 // TODO: find good default value for that
  val InitialMaxVelocity = 1.0 // TODO: change value? Or expose as constructor parameter?

  override def getSyntax: Syntax = reporterSyntax(
    right = List(
      PatchsetType, // the choices (patch sets only for PSO choosers)
      ReporterType // a reporter that should report either a single chooser or a list of choosers to imitate
    ),
    ret = WildcardType
  )
  override def report(args: Array[Argument], context: Context): AnyRef = {
    def extractor(f: Patch => Double): FeatureExtractor[AnyRef, Null] = (o, _) => f(o.asInstanceOf[Patch])
    new ImitatorChooserObject(
      new ParticleSwarm[AnyRef, Double, Null](
        Array(extractor(_.pxcor), extractor(_.pycor)),
        (pos: Array[Double]) => context.world.getPatchAt(pos(0), pos(1)),
        DefaultWeightOfImitation,
        DefaultWeightOfMemory,
        DefaultInertia,
        InitialMaxVelocity,
        NullIgnoringMonoBelief,
        IdentityRewardFunction,
        args(0).getOptionsArray(context.getRNG),
        new SplittableRandom(context.getRNG.nextLong()),
        null
      ),
      args(1).getReporter
    )
  }

  object NullIgnoringMonoBelief extends MonoBelief[AnyRef, Double, Null](IdentityRewardFunction) {
    override def observe(observation: Observation[AnyRef, Double, Null]): Unit =
      if (observation != null) super.observe(observation)
  }
}
