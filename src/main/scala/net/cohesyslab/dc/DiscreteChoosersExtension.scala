package net.cohesyslab.dc

import net.cohesyslab.dc.bandits.EpsilonGreedyChooserPrim
import net.cohesyslab.dc.bandits.SetSigmaPrim
import net.cohesyslab.dc.bandits.SetTemperaturePrim
import net.cohesyslab.dc.bandits.SigmaPrim
import net.cohesyslab.dc.bandits.SoftmaxChooserPrim
import net.cohesyslab.dc.bandits.TemperaturePrim
import net.cohesyslab.dc.bandits.Ucb1ChooserPrim
import net.cohesyslab.dc.imitators.ExploreExploitImitateChooserPrim
import net.cohesyslab.dc.imitators.ImitationProbabilityPrim
import net.cohesyslab.dc.imitators.SetImitationProbabilityPrim

object DiscreteChoosersExtension {
  val name = "dc"
}

class DiscreteChoosersExtension extends ExtensionClassManager(
  // Common to all choosers:
  ObservePrim,
  LastObservationPrim,
  ChoicePrim,
  OptionsPrim,
  ValuePrim,
  OptionValuesPrim,
  BestOptionPrim,
  BestOptionsPrim,

  // More specific chooser prims
  ExplorationProbabilityPrim,
  SetExplorationProbabilityPrim,

  // Epsilon-greedy bandit:
  EpsilonGreedyChooserPrim,

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
  ImitationProbabilityPrim,
  SetImitationProbabilityPrim
)