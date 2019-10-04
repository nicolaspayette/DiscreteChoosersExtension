package net.cohesyslab

import io.github.carrknight.utils.RewardFunction
import org.nlogo.agent.AgentSet
import org.nlogo.api.Agent
import org.nlogo.api.Argument
import org.nlogo.api.Dump
import org.nlogo.api.ExtensionException
import org.nlogo.api.MersenneTwisterFast
import org.nlogo.core.LogoList

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.reflect.classTag

package object dc {

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

    def getChooserAs[T: ClassTag]: T = arg.get.as[ChooserObject].chooser.as[T]

    def getOptionsArray(rng: MersenneTwisterFast): Array[AnyRef] = arg.get match {
      case agentSet: AgentSet => agentSet.toOptionsArray(rng)
      case logoList: LogoList => logoList.toOptionsArray
    }

  }

  implicit class RichAgentSet(agentSet: AgentSet) {
    def toOptionsArray[T >: Agent <: AnyRef : ClassTag](rng: MersenneTwisterFast): Array[T] = {
      if (agentSet.isEmpty) throw new ExtensionException("A chooser cannot be created with an empty set of options.")
      val builder = new mutable.ArrayBuilder.ofRef[T]()
      val iterator = agentSet.shufflerator(rng)
      while (iterator.hasNext) builder += iterator.next()
      builder.result()
    }
  }

  implicit class RichLogoList(logoList: LogoList) {
    def toOptionsArray: Array[AnyRef] = {
      if (logoList.isEmpty) throw new ExtensionException("A chooser cannot be created with an empty list of options.")
      logoList.toArray
    }
  }


    /**
   * This is the simplest possible reward function, which only makes sure that the
   * result passed to it is a number and returns its value as a double.
   */
  object SimpleRewardFunction extends RewardFunction[AnyRef, AnyRef, Null] {
    override def extractUtility(
      optionTaken: AnyRef,
      experimentResult: AnyRef,
      contextObject: Null
    ): Double =
      experimentResult match {
        case n: Number => n.doubleValue()
        case obj => throw new ExtensionException("not a number: " + Dump.logoObject(obj))
      }
  }

}
