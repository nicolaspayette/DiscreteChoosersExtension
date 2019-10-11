package net.cohesyslab.dc

import net.cohesyslab.dc.bandits.EpsilonGreedyChooserPrim
import net.cohesyslab.dc.bandits.EpsilonPrim
import net.cohesyslab.dc.bandits.SetEpsilonPrim
import net.cohesyslab.dc.bandits.SetSigmaPrim
import net.cohesyslab.dc.bandits.SetTemperaturePrim
import net.cohesyslab.dc.bandits.SigmaPrim
import net.cohesyslab.dc.bandits.SoftmaxChooserPrim
import net.cohesyslab.dc.bandits.TemperaturePrim
import net.cohesyslab.dc.bandits.Ucb1ChooserPrim
import net.cohesyslab.dc.imitators.ExplorationProbabilityPrim
import net.cohesyslab.dc.imitators.ExploreExploitImitateChooserPrim
import net.cohesyslab.dc.imitators.ImitationProbabilityPrim
import net.cohesyslab.dc.imitators.SetExplorationProbabilityPrim
import net.cohesyslab.dc.imitators.SetImitationProbabilityPrim

object DiscreteChoosersExtension {
  val name = "dc"
}

class DiscreteChoosersExtension extends ExtensionClassManager(
  // Common to all choosers:
  ObservePrim,
  LastObservationPrim,
  ChoicePrim,
  ExpectationsPrim,
  OptionsPrim,
  BestOptionPrim,

  // Epsilon-greedy bandit:
  EpsilonGreedyChooserPrim,
  EpsilonPrim,
  SetEpsilonPrim,

  // SoftMax bandit:
  SoftmaxChooserPrim,
  TemperaturePrim,
  SetTemperaturePrim,

  // UCB bandit:
  Ucb1ChooserPrim,
  SigmaPrim,
  SetSigmaPrim,

  // Explore-exploit-imitate
  ExploreExploitImitateChooserPrim,
  ExplorationProbabilityPrim,
  SetExplorationProbabilityPrim,
  ImitationProbabilityPrim,
  SetImitationProbabilityPrim
)