/*
 * Copyright 2016 TU Dortmund
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.learnlib.alex.core.learner;

import de.learnlib.alex.algorithms.LearnAlgorithmFactory;
import de.learnlib.alex.core.dao.LearnerResultDAO;
import de.learnlib.alex.core.entities.ExecuteResult;
import de.learnlib.alex.core.entities.LearnerResult;
import de.learnlib.alex.core.entities.LearnerResultStep;
import de.learnlib.alex.core.entities.Statistics;
import de.learnlib.alex.core.entities.Symbol;
import de.learnlib.alex.core.entities.learnlibproxies.AlphabetProxy;
import de.learnlib.alex.core.entities.learnlibproxies.DefaultQueryProxy;
import de.learnlib.alex.core.learner.connectors.ConnectorContextHandler;
import de.learnlib.alex.core.learner.connectors.ConnectorManager;
import de.learnlib.alex.exceptions.NotFoundException;
import de.learnlib.api.EquivalenceOracle;
import de.learnlib.api.LearningAlgorithm.MealyLearner;
import de.learnlib.api.SUL;
import de.learnlib.cache.sul.SULCache;
import de.learnlib.cache.sul.SULCaches;
import de.learnlib.mapper.ContextExecutableInputSUL;
import de.learnlib.mapper.Mappers;
import de.learnlib.mapper.api.ContextExecutableInput;
import de.learnlib.oracles.DefaultQuery;
import de.learnlib.oracles.SULOracle;
import net.automatalib.automata.transout.MealyMachine;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.ThreadContext;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

/**
 * Thread to run a learning process. It needs to be a Thread so that the server can still deal with other requests.
 * This class contains the actual learning loop.
 */
public class LearnerThread extends Thread {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Marker LEARNER_MARKER = MarkerManager.getMarker("LEARNER");

    /**
     * Is the thread still running?
     */
    private boolean finished;

    /**
     * Mapper to match the Alphabet to the right symbols.
     */
    private final SymbolMapper symbolMapper;

    /**
     * The System Under Learning used to do the learning on.
     */
    private final AlexSUL<String, String> sul;

    /**
     * The actual System Under Learning put into a cache.
     */
    private SULCache<String, String> cachedSUL;

    /**
     * The DAO to remember the learn results.
     */
    private final LearnerResultDAO learnerResultDAO;

    /**
     * The result of this learning thread.
     */
    private final LearnerResult result;

    /**
     * The current step of the learn result.
     */
    private LearnerResultStep currentStep;

    /**
     * The learner to use during the learning.
     */
    private final MealyLearner<String, String> learner;

    /**
     * The Alphabet of the Symbols which will be used during the learning.
     */
    private final Alphabet<String> sigma;

    /**
     * The membership oracle.
     */
    private final SULOracle<String, String> mqOracle;

    /**
     * Constructor to set the LearnerThread up.
     *
     * @param learnerResultDAO
     *         The DAO to persists the results.
     * @param result
     *         The result to update, including the proper configuration.
     * @param context
     *         The context of the SUL. If this context is a counter, the 'amountOfResets' field will be set correctly.
     */
    public LearnerThread(LearnerResultDAO learnerResultDAO, LearnerResult result, ConnectorContextHandler context) {
        this.finished = false;
        this.learnerResultDAO = learnerResultDAO;
        this.result = result;
        this.currentStep = result.getSteps().get(result.getSteps().size() - 1); // get the latest step

        Symbol[] symbolsArray = readSymbolArray(); // use the symbols in the result to create the symbol array.
        this.symbolMapper = new SymbolMapper(symbolsArray);
        this.sigma = symbolMapper.getAlphabet();
        this.result.setSigma(AlphabetProxy.createFrom(sigma));

        ContextExecutableInputSUL<ContextExecutableInput<ExecuteResult, ConnectorManager>,
                                  ExecuteResult,
                                  ConnectorManager>
                ceiSUL = new ContextExecutableInputSUL<>(context);
        SUL<String, String> mappedSUL = Mappers.apply(symbolMapper, ceiSUL);
        this.cachedSUL = SULCaches.createCache(this.sigma, mappedSUL);
        this.sul = new AlexSUL<>(cachedSUL);

        this.mqOracle = new SULOracle<>(sul);

        LearnAlgorithmFactory algorithm = result.getAlgorithmFactory();
        this.learner = algorithm.createLearner(sigma, mqOracle);
    }

    /**
     * Advanced constructor to set the LearnerThread up.
     * Most likely to be used when resuming a learn process.
     *
     * @param learnerResultDAO The DAO to persists the results.
     * @param result           The result to update, including the proper configuration.
     * @param existingSUL      The existing SULCache.
     * @param learner          Don't create a new learner, instead use this one.
     * @param symbols          The Symbols to use.
     */
    public LearnerThread(LearnerResultDAO learnerResultDAO, LearnerResult result, SULCache<String, String> existingSUL,
                         MealyLearner<String, String> learner, Symbol... symbols) {
        this.finished = false;
        this.learnerResultDAO = learnerResultDAO;
        this.result = result;
        this.currentStep = result.getSteps().get(result.getSteps().size() - 1); // get the latest step

        this.symbolMapper = new SymbolMapper(symbols);
        this.sigma = symbolMapper.getAlphabet();
        result.setSigma(AlphabetProxy.createFrom(sigma));

        this.cachedSUL = existingSUL;
        this.sul = new AlexSUL<>(cachedSUL);

        this.mqOracle = new SULOracle<>(sul);

        this.learner = learner;
    }

    private Symbol[] readSymbolArray() {
        Set<Symbol> symbols = result.getSymbols();
        return symbols.toArray(new Symbol[symbols.size()]);
    }

    /**
     * Returns the current status of the Thread.
     *
     * @return true, if the Thread is still active; false otherwise.
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Get the result the Thread, which also contains the current configuration.
     *
     * @return The current result.
     */
    public LearnerResult getResult() {
        return result;
    }

    /**
     * Get the actual SUL used for the learning process wrapped in a cache..
     *
     * @return The SUL used for the learning process put into a cache.
     */
    public SULCache<String, String> getCachedSUL() {
        return cachedSUL;
    }

    /**
     * Get the learner used by the LearnerThread.
     *
     * @return The learner used by the thread.
     */
    public MealyLearner<String, String> getLearner() {
        return learner;
    }

    /**
     * Get the symbols used by the LearnerThread.
     *
     * @return The Symbols used by the thread.
     */
    public List<Symbol> getSymbols() {
        return symbolMapper.getSymbols();
    }

    @Override
    public void run() {
        ThreadContext.put("userId", String.valueOf(result.getUserId()));
        ThreadContext.put("testNo", String.valueOf(result.getTestNo()));
        ThreadContext.put("indent", "");
        LOGGER.traceEntry();
        LOGGER.info(LEARNER_MARKER, "Started a learn thread.");

        try {
            learn();
        } catch (Exception e) {
            LOGGER.warn(LEARNER_MARKER, "Something in the LearnerThread went wrong:", e);

            String message = e.getMessage();
            if (message == null) {
                message = e.getClass().getName();
            }
            currentStep.setErrorText(message);
            learnerResultDAO.saveStep(result, currentStep);

            sul.post();
        } finally {
            finished = true;
            LOGGER.info(LEARNER_MARKER, "The learn thread has finished.");
            LOGGER.traceExit();
        }
    }

    /**
     * Get the {@link AlexSUL}.
     *
     * @return The active ResetCounterSUL
     */
    public AlexSUL<String, String> getSul() {
        return sul;
    }

    private void learn() throws NotFoundException {
        LOGGER.traceEntry();
        do {
            learnOneStep();
        } while (continueLearning());
        LOGGER.traceExit();
    }

    private boolean continueLearning() {
        return currentStep.getCounterExample() != null
                && (currentStep.getStepsToLearn() > 0 || currentStep.getStepsToLearn() == -1)
                && !Thread.interrupted();
    }

    private void learnOneStep() throws NotFoundException {
        LOGGER.traceEntry();

        if (result.getStatistics().getDuration().getTotal() == 0L) {
            learnFirstStep();
        } else {
            learnSuccessiveStep();
        }
        learnerResultDAO.saveStep(result, currentStep);

        LOGGER.traceExit();
    }

    private void learnFirstStep() throws NotFoundException {
        LOGGER.traceEntry();

        // init. statistics
        Statistics statistics = currentStep.getStatistics();
        statistics.setStartDate(ZonedDateTime.now());
        statistics.setStartTime(System.nanoTime());
        statistics.setEqsUsed(0L);
        statistics.setDuration(new Statistics.DetailedStatistics());
        statistics.setMqsUsed(new Statistics.DetailedStatistics());
        statistics.setSymbolsUsed(new Statistics.DetailedStatistics());
        result.getStatistics().setStartDate(statistics.getStartDate());

        // learn
        learner.startLearning();
        storeLearnerMetaData();

        // search counter example
        findCounterExample();
        storeCounterExampleSearchMetaData();

        LOGGER.traceExit();
    }

    private void learnSuccessiveStep() throws NotFoundException {
        LOGGER.traceEntry();

        // When we continue to learn, we have to create a new step based on the previous one.
        // Otherwise, if the learning is resumed, a new step with the new configuration, will already exists.
        LearnerResultStep previousStep;
        if (currentStep.getStatistics().getDuration().getTotal() > 0) {
            previousStep = currentStep;
            currentStep  = learnerResultDAO.createStep(result);
        } else {
            previousStep = result.getSteps().get((int) (currentStep.getStepNo() - 1));
        }

        // set the start date and the start time:
        // - The start date acts as timestamp and is public.
        // - The start time is more accurate (nanoseconds) and will be only used internally to calculate the duration.
        Statistics statistics = currentStep.getStatistics();
        statistics.setStartDate(ZonedDateTime.now());
        statistics.setStartTime(System.nanoTime());

        // Get the counter example from the previous step and use it to refine the hypothesis (if possible).
        DefaultQueryProxy counterExample = previousStep.getCounterExample();
        if (counterExample != null) {
            learner.refineHypothesis(counterExample.createDefaultProxy());
            storeLearnerMetaData();
        }

        // search counter example
        findCounterExample();
        storeCounterExampleSearchMetaData();

        LOGGER.traceExit();
    }

    private void storeLearnerMetaData() throws NotFoundException {
        // statistics
        Statistics statistics = currentStep.getStatistics();

        long startTime = statistics.getStartTime();
        long currentTime = System.nanoTime();
        statistics.getDuration().setLearner(currentTime - startTime);
        LOGGER.info(LEARNER_MARKER, "Duration of the learning: {} (start: {}, end: {}).",
                    statistics.getDuration(), startTime, currentTime);

        statistics.getMqsUsed().setLearner(sul.getResetCount());
        statistics.getSymbolsUsed().setLearner(sul.getSymbolUsedCount());
        sul.resetCounter();

        // algorithm information
        currentStep.createHypothesisFrom(learner.getHypothesisModel());

        LearnAlgorithmFactory algorithm = result.getAlgorithmFactory();
        String algorithmInformation;
        try {
            algorithmInformation = algorithm.getInternalData(learner);
        } catch (IllegalStateException e) { // algorithm has no internal data to show
            algorithmInformation = "";
        }
        currentStep.setAlgorithmInformation(algorithmInformation);
    }

    private void findCounterExample() {
        EquivalenceOracle<MealyMachine<?, String, ?, String>, String, Word<String>> eqOracle;
        eqOracle = currentStep.getEqOracle().createEqOracle(mqOracle);
        DefaultQuery<String, Word<String>> newCounterExample;
        newCounterExample = eqOracle.findCounterExample(learner.getHypothesisModel(), sigma);

        // remember the counter example, if any
        if (newCounterExample == null) {
            currentStep.setCounterExample(null);
        } else {
            currentStep.setCounterExample(DefaultQueryProxy.createFrom(newCounterExample));
        }
        LOGGER.info(LEARNER_MARKER, "The new counter example is '{}'.", newCounterExample);
    }

    private void storeCounterExampleSearchMetaData() {
        // statistics
        Statistics statistics = currentStep.getStatistics();

        statistics.setEqsUsed(currentStep.getStatistics().getEqsUsed() + 1);

        long startTime = statistics.getStartTime();
        long currentTime = System.nanoTime();
        long duration = currentTime - startTime - statistics.getDuration().getLearner();
        statistics.getDuration().setEqOracle(duration);
        LOGGER.info(LEARNER_MARKER, "Duration of the eq oracle: {}.", duration);

        statistics.getMqsUsed().setEqOracle(sul.getResetCount());
        statistics.getSymbolsUsed().setEqOracle(sul.getSymbolUsedCount());
        sul.resetCounter();
    }

}
