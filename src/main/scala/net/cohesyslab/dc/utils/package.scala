package net.cohesyslab.dc

import io.github.carrknight.Chooser
import io.github.carrknight.utils.RewardFunction
import org.nlogo.agent.AgentSet
import org.nlogo.api.Agent
import org.nlogo.api.Argument
import org.nlogo.api.Dump
import org.nlogo.api.ExtensionException
import org.nlogo.api.MersenneTwisterFast
import org.nlogo.api.ScalaConversions._
import org.nlogo.core.LogoList

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.reflect.classTag

package object utils {

  implicit class RichAnyRef(anyRef: AnyRef) {

    /** Provides a shorthand casting mechanism that raises an appropriate extension exception */
    def as[T: ClassTag]: T = anyRef match {
      case t: T => t
      case obj =>
        throw new ExtensionException(
          "object " + Dump.logoObject(obj) + " should be of type " +
            classTag[T].runtimeClass.getSimpleName
        )
    }
  }

  implicit class RichArgument(arg: Argument) {

    def getChooser: ChooserObject = arg.get.as[ChooserObject]

    def getChooserAs[T <: Chooser[_, _, _] : ClassTag]: T = arg.get.as[ChooserObject].chooser.as[T]

    def getOptionsArray(rng: MersenneTwisterFast): Array[AnyRef] = arg.get match {
      case agentSet: AgentSet => agentSet.toOptionsArray(rng)
      case logoList: LogoList => logoList.toOptionsArray
    }

  }

  implicit class RichAgentSet(agentSet: AgentSet) {
    def toOptionsArray[T >: Agent <: AnyRef : ClassTag](rng: MersenneTwisterFast): Array[T] = {
      if (agentSet.isEmpty) throw new ExtensionException("A chooser cannot be created with an empty agentset of options.")
      val builder = new mutable.ArrayBuilder.ofRef[T]()
      val iterator = agentSet.shufflerator(rng)
      while (iterator.hasNext) builder += iterator.next()
      builder.result()
    }
  }

  implicit class RichLogoList(logoList: LogoList) {
    def toOptionsArray: Array[AnyRef] = {
      if (logoList.isEmpty)
        throw new ExtensionException("A chooser cannot be created with an empty list of options.")
      if (logoList.distinct.size != logoList.size) {
        val duplicates = logoList.groupBy(identity).filter(_._2.size > 1).keys.toSeq
        throw new ExtensionException(
          "The list of options should only contain unique items but the following duplicates were found: " +
            Dump.logoObject(logoList.filter(duplicates.contains).toLogoList)
        )
      }
      logoList.toArray
    }
  }

  /** A simple reward function returning the non-transformed reward */
  object IdentityRewardFunction extends RewardFunction[AnyRef, Double, Null] {
    override def extractUtility(o: AnyRef, r: Double, c: Null): Double = r
  }

}
