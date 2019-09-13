package net.cohesyslab.dc

import io.github.carrknight.bandits.AbstractBanditAlgorithm
import io.github.carrknight.heatmaps.BeliefState
import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.Reporter
import org.nlogo.core.LogoList
import org.nlogo.core.Syntax
import org.nlogo.core.Syntax.ListType
import org.nlogo.core.Syntax.WildcardType
import org.nlogo.core.Syntax.reporterSyntax

import scala.collection.JavaConverters._

object GetBeliefsPrim extends Reporter {

  override def getSyntax: Syntax = reporterSyntax(right = List(WildcardType), ret = ListType)

  override def report(args: Array[Argument], context: Context): AnyRef = {
    val bandit = args(0).getChooserAs[AbstractBanditAlgorithm[AnyRef, _, Null]]
    val state: BeliefState[AnyRef, _, Null] = bandit.getBanditState
    LogoList.fromVector(
      bandit
        .getOptionsAvailable.entrySet.asScala.toVector
        .sortBy(_.getValue)
        .map(entry => LogoList(entry.getKey, Double.box(state.predict(entry.getKey, null)))))
  }

}
