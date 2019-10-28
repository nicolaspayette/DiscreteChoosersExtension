package net.cohesyslab.dc.imitators

import java.util.SplittableRandom

import io.github.carrknight.Observation
import io.github.carrknight.heatmaps.regression.FeatureExtractor
import io.github.carrknight.imitators.MonoBelief
import io.github.carrknight.imitators.ParticleSwarm
import net.cohesyslab.dc.OptionValue
import net.cohesyslab.dc.imitators.ParticleSwarmChooserPrim.DefaultInertia
import net.cohesyslab.dc.imitators.ParticleSwarmChooserPrim.DefaultWeightOfImitation
import net.cohesyslab.dc.imitators.ParticleSwarmChooserPrim.DefaultWeightOfMemory
import net.cohesyslab.dc.imitators.ParticleSwarmChooserPrim.InitialMaxVelocity
import net.cohesyslab.dc.imitators.ParticleSwarmChooserPrim.NullIgnoringMonoBelief
import net.cohesyslab.dc.utils.IdentityRewardFunction
import net.cohesyslab.dc.utils.RichArgument
import org.nlogo.api.AnonymousReporter
import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.ExtensionException
import org.nlogo.api.Patch
import org.nlogo.api.Reporter
import org.nlogo.core.Syntax
import org.nlogo.core.Syntax.PatchsetType
import org.nlogo.core.Syntax.ReporterType
import org.nlogo.core.Syntax.WildcardType
import org.nlogo.core.Syntax.reporterSyntax

import scala.collection.JavaConverters._

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
    new ParticleSwarmChooser(
      args(0).getOptionsArray(context.getRNG),
      context.getRNG.nextLong(),
      (pos: Array[Double]) => context.world.getPatchAt(pos(0), pos(1)),
      args(1).getReporter
    )
  }

  object NullIgnoringMonoBelief extends MonoBelief[AnyRef, Double, Null](IdentityRewardFunction) {
    override def observe(observation: Observation[AnyRef, Double, Null]): Unit =
      if (observation != null) super.observe(observation)
  }

}

object ParticleSwarmChooser {
  def extractors: Array[FeatureExtractor[AnyRef, Null]] = Array(extractor(_.pxcor), extractor(_.pycor))
  private def extractor(f: Patch => Double): FeatureExtractor[AnyRef, Null] = (o, _) => f(o.asInstanceOf[Patch])
}

class ParticleSwarmChooser(
  options: Array[AnyRef],
  randomSeed: Long,
  inverseProjector: java.util.function.Function[Array[Double], AnyRef],
  observedChoosersReporter: AnonymousReporter
) extends ImitatorChooser(
  new ParticleSwarm[AnyRef, Double, Null](
    ParticleSwarmChooser.extractors,
    inverseProjector,
    DefaultWeightOfImitation,
    DefaultWeightOfMemory,
    DefaultInertia,
    InitialMaxVelocity,
    NullIgnoringMonoBelief,
    IdentityRewardFunction,
    options,
    new SplittableRandom(randomSeed),
    null
  ),
  observedChoosersReporter
) {
  def extractor(f: Patch => Double): FeatureExtractor[AnyRef, Null] = (o, _) => f(o.asInstanceOf[Patch])

  def options: Vector[AnyRef] =
    delegate.getOptionsAvailable.asScala.toVector // TODO: either get this through reflection or make send PR with getter to Ernesto

  def isValidOption(option: AnyRef): Boolean =
    delegate.getOptionsAvailable.asScala.exists(_ == option) // TODO: either get this through reflection or make send PR with getter to Ernesto

  def optionValue(option: AnyRef): OptionValue =
    throw new ExtensionException("Particle swarm choosers do not maintain option values.")

}
