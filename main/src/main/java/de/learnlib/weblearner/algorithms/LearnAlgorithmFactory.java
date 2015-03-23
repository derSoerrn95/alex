package de.learnlib.weblearner.algorithms;

import de.learnlib.api.LearningAlgorithm;
import de.learnlib.oracles.SULOracle;
import net.automatalib.words.Alphabet;

public interface LearnAlgorithmFactory {

    public LearningAlgorithm.MealyLearner<String, String> createLearner(Alphabet<String> sigma,
                                                                        SULOracle<String, String> oracle);

    public String getInternalData(LearningAlgorithm.MealyLearner<String, String> learner);

}
