package de.learnlib.weblearner.learner;

import de.learnlib.api.EquivalenceOracle;
import de.learnlib.api.LearningAlgorithm.MealyLearner;
import de.learnlib.api.SUL;
import de.learnlib.mapper.ContextExecutableInputSUL;
import de.learnlib.mapper.Mappers;
import de.learnlib.mapper.api.ContextExecutableInput;
import de.learnlib.oracles.DefaultQuery;
import de.learnlib.oracles.SULOracle;
import de.learnlib.weblearner.dao.LearnerResultDAO;
import de.learnlib.weblearner.entities.LearnAlgorithms;
import de.learnlib.weblearner.entities.LearnerResult;
import de.learnlib.weblearner.entities.Symbol;
import de.learnlib.weblearner.learner.connectors.MultiConnector;
import de.learnlib.weblearner.learner.connectors.MultiContextHandler;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;

import java.util.Date;
import java.util.List;

/**
 * Thread to run a learning process. It needs to be a Thread so that the server can still deal with other requests.
 * This class contains the actual learning loop.
 * 
 * @param <C> Type used in the Symbol to implement the symbol execution.
 */
public class LearnerThread<C> extends Thread {

    /** Is the thread still running? */
    private boolean active;

    /** Mapper to match the Alphabet to the right symbols. */
    private final SymbolMapper<C> symbolMapper;

    /** The SUL the thread will learn. */
    private  final SUL<ContextExecutableInput<String, MultiConnector>, String> ceiSUL;

    /** The context of the SUL. */
    private final ContextExecutableInputSUL.ContextHandler<MultiConnector> context;

    /** The System Under Learning used to do the actual learning. */
    private final SUL<String, String> sul;

    /** The DAO to remember the learn results. */
    private final LearnerResultDAO learnerResultDAO;

    /** The result of this learning thread. */
    private final LearnerResult result;

    /** The learner to use during the learning. */
    private final MealyLearner<String, String> learner;

    /** The Alphabet of the Symbols which will be used during the learning. */
    private final Alphabet<String> sigma;

    /** The membership oracle. */
    private final SULOracle<String, String> oracle;

    /**
     * Constructor to set the LearnerThread up.
     *  @param learnerResultDAO
     *         The DAO to persists the results.
     * @param result
     *         The result to update, including the proper configuration.
     * @param context
 *         The context of the SUL. If this context is a counter, the 'amountOfResets' field will be set correctly.
     */
    public LearnerThread(LearnerResultDAO learnerResultDAO, LearnerResult result,
                         ContextExecutableInputSUL.ContextHandler<MultiConnector> context) {
        this.active = false;
        this.learnerResultDAO = learnerResultDAO;
        this.result = result;
        this.ceiSUL = new ContextExecutableInputSUL<>(context);
        this.context = context;
        this.symbolMapper = new SymbolMapper<>(result.getConfiguration().getSymbols().toArray(new Symbol[result.getConfiguration().getSymbols().size()]));
        this.sul = Mappers.apply(symbolMapper, ceiSUL);

        this.sigma = symbolMapper.getAlphabet();
        result.setSigma(sigma);
        oracle = new SULOracle<>(sul);

        LearnAlgorithms algorithm = result.getConfiguration().getAlgorithm();
        this.learner = LearnerAlgorithmFactory.createLearner(algorithm, sigma, oracle);
    }

    /**
     * Advanced constructor to set the LearnerThread up.
     *
     * @param learnerResultDAO
     *         The DAO to persists the results.
     * @param result
     *         The result to update, including the proper configuration.
     * @param context
     *         The context of the SUL. If this context is a counter, the 'amountOfResets' field will be set correctly.
     * @param learner
     *         Don't create a new learner, instead use this one.
     * @param symbols
     *         The Symbols to use.
     */
    public LearnerThread(LearnerResultDAO learnerResultDAO,
                         LearnerResult result,
                         ContextExecutableInputSUL.ContextHandler<MultiConnector> context,
                         MealyLearner<String, String> learner,
                         Symbol... symbols) {
        this.active = false;
        this.learnerResultDAO = learnerResultDAO;
        this.result = result;
        this.context = context;
        this.ceiSUL = new ContextExecutableInputSUL<>(context);
        this.symbolMapper = new SymbolMapper<>(symbols);
        this.sul = Mappers.apply(symbolMapper, ceiSUL);

        this.sigma = symbolMapper.getAlphabet();
        result.setSigma(sigma);
        this.oracle = new SULOracle<>(sul);

        this.learner = learner;
    }

    /**
     * Returns the current status of the Thread.
     *
     * @return true, if the Thread is still active; false otherwise.
     */
    public boolean isActive() {
        return active;
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
     * Get the context used for the SUL.
     *
     * @return The current context.
     */
    public ContextExecutableInputSUL.ContextHandler<MultiConnector> getContext() {
        return context;
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
        active = true;
        learn();
        active = false;
    }

    private void learn() {
        int maxAmountOfStepsToLearn = result.getConfiguration().getMaxAmountOfStepsToLearn();
        long currentStepNo = result.getStepNo() == null ? 0L : result.getStepNo();
        long maxStepCount =  currentStepNo + maxAmountOfStepsToLearn;
        boolean shouldDoAnotherStep;

        do {
            shouldDoAnotherStep = learnOneStep();
        } while (continueLearning(shouldDoAnotherStep, maxStepCount));
    }

    private boolean learnOneStep() {
        boolean shouldDoAnotherStep = false;

        result.setStartTime(new Date());

        if (result.getStepNo() == null || result.getStepNo().equals(0L)) {
            learnFirstHypothesis();
            shouldDoAnotherStep = true;
        } else {
            DefaultQuery<String, Word<String>> counterExample = findCounterExample();

            if (counterExample != null) {
                refineHypothesis(counterExample);
                shouldDoAnotherStep = true;
            }
        }

        return shouldDoAnotherStep;
    }

    private boolean continueLearning(boolean shouldDoAnotherStep, long maxStepCount) {
        int maxAmountOfStepsToLearn = result.getConfiguration().getMaxAmountOfStepsToLearn();
        return shouldDoAnotherStep
                && (maxAmountOfStepsToLearn == 0 || result.getStepNo() < maxStepCount)
                && !Thread.interrupted();
    }

    private void learnFirstHypothesis() {
        learner.startLearning();
        result.createHypothesisFrom(learner.getHypothesisModel());
        rememberMetaData();
        learnerResultDAO.create(result);
    }

    private DefaultQuery<String, Word<String>> findCounterExample() {
        EquivalenceOracle randomWords = result.getConfiguration().getEqOracle().createEqOracle(oracle);
        return randomWords.findCounterExample(learner.getHypothesisModel(), sigma);
    }

    private void refineHypothesis(DefaultQuery<String, Word<String>> counterExample) {
        learner.refineHypothesis(counterExample);
        result.createHypothesisFrom(learner.getHypothesisModel());
        rememberMetaData();
        learnerResultDAO.update(result);
    }

    private void rememberMetaData() {
        long startTime = result.getStartTime().getTime();
        long currentTime = new Date().getTime();
        result.setDuration(currentTime - startTime);
        if (context instanceof MultiContextHandler) {
            result.setAmountOfResets(((MultiContextHandler) context).getCounter() - result.getAmountOfResets());
        } else {
            result.setAmountOfResets(-1);
        }
    }

}
