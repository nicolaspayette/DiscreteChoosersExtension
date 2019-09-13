package net.cohesyslab

import io.github.carrknight.utils.RewardFunction
import org.nlogo.api.Agent
import org.nlogo.api.AgentSet
import org.nlogo.api.Argument
import org.nlogo.api.Dump
import org.nlogo.api.ExtensionException
import org.nlogo.core.LogoList

import scala.collection.JavaConverters.iterableAsScalaIterableConverter
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

    def getChooserAs[T: ClassTag]: T = arg.get.as[ChooserObject].chooser.as[T]

    def getOptionsArray: Array[AnyRef] = arg.get match {
      case agentSet: AgentSet => agentSet.toArray
      case logoList: LogoList => logoList.toArray
    }

  }

  implicit class RichAgentSet(agentSet: AgentSet) {
    def toArray[T >: Agent : ClassTag]: Array[T] = {
      val result = new Array[T](agentSet.count)
      agentSet.agents.asScala.copyToArray(result)
      result
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
