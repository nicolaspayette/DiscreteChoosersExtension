package net.cohesyslab

import io.github.carrknight.Chooser
import io.github.carrknight.utils.RewardFunction
import org.nlogo.agent.AgentSet
import org.nlogo.api.Agent
import org.nlogo.api.Argument
import org.nlogo.api.Command
import org.nlogo.api.Context
import org.nlogo.api.Dump
import org.nlogo.api.ExtensionException
import org.nlogo.api.MersenneTwisterFast
import org.nlogo.api.Reporter
import org.nlogo.api.ScalaConversions._
import org.nlogo.core.LogoList
import org.nlogo.core.Syntax
import org.nlogo.core.Syntax.NumberType
import org.nlogo.core.Syntax.WildcardType
import org.nlogo.core.Syntax.commandSyntax
import org.nlogo.core.Syntax.reporterSyntax

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.reflect.classTag

package object dc {

  /** A simple assertion that throws an ExtensionException if the condition is not met */
  def check(condition: Boolean, message: => String): Unit =
    if (!condition) throw new ExtensionException(message)

  class NumberGetter[T <: Chooser[_, _, _] : ClassTag](get: T => Any) extends Reporter {
    override def getSyntax: Syntax =
      reporterSyntax(right = List(WildcardType), ret = NumberType)
    override def report(args: Array[Argument], context: Context): AnyRef =
      get(args(0).getChooserAs[T]).toLogoObject
  }

  class NumberSetter[T <: Chooser[_, _, _] : ClassTag](set: (T, Double) => Unit) extends Command {
    override def getSyntax: Syntax =
      commandSyntax(List(WildcardType, NumberType))
    override def perform(args: Array[Argument], context: Context): Unit =
      set(args(0).getChooserAs[T], args(1).getDoubleValue)
  }

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

  implicit class RichDouble(val d: Double) extends AnyVal {
    def inRange(lowerBound: Double, upperBound: Double) =
      lowerBound <= d && d <= upperBound;
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
      if (agentSet.isEmpty) throw new ExtensionException("A chooser cannot be created with an empty set of options.")
      val builder = new mutable.ArrayBuilder.ofRef[T]()
      val iterator = agentSet.shufflerator(rng)
      while (iterator.hasNext) builder += iterator.next()
      builder.result()
    }
  }

  implicit class RichLogoList(logoList: LogoList) {
    def toOptionsArray: Array[AnyRef] = {
      check(!logoList.isEmpty, "A chooser cannot be created with an empty list of options.")
      logoList.toArray
    }
  }

  /** A simple reward function returning the non-transformed reward */
  object IdentityRewardFunction extends RewardFunction[AnyRef, Double, Null] {
    override def extractUtility(o: AnyRef, r: Double, c: Null): Double = r
  }

}
