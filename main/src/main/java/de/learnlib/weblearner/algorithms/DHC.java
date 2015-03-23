package de.learnlib.weblearner.algorithms;

import de.learnlib.algorithms.dhc.mealy.MealyDHCBuilder;
import de.learnlib.api.LearningAlgorithm;
import de.learnlib.oracles.SULOracle;
import de.learnlib.weblearner.annotations.LearnAlgorithm;
import net.automatalib.words.Alphabet;

@LearnAlgorithm(name = "DHC")
public class DHC implements LearnAlgorithmFactory {

    @Override
    public LearningAlgorithm.MealyLearner<String, String> createLearner(Alphabet<String> sigma,
                                                                        SULOracle<String, String> oracle) {
        return new MealyDHCBuilder<String, String>().withAlphabet(sigma).withOracle(oracle).create();
    }

    @Override
    public String getInternalData(LearningAlgorithm.MealyLearner<String, String> learner) {
        throw new IllegalStateException("DHC has no internal data structures");
    }

}
