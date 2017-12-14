# Learning

In order to start learning an application, a learn process has to be modeled. Such a process always consists of the
following components:

* An input alphabet (set of symbols)
* A symbol to reset the application
* An algorithm
* A parametrized equivalence oracle
* A maximum amount of steps to learn
* A specification of a web browser

<dl>
    <dt>Algorithm</dt>
    <dd>
        There are currently five algorithms supported: L*, Discrimination Tree, DHC, Kearns & Vazirani and TTT.
    </dd>
    <dt>Equivalence oracle</dt>
    <dd>
        ALEX supports five kinds of oracles. Those are: <em>Random Word</em>, <em>Complete</em>, <em>W-Method</em>, <em>Sample</em> and <em>Hypothesis</em>. 
        The first three oracles approximate equivalence queries automatically to find counterexamples while when using
        <em>Sample</em>, you are asked to search and enter them by yourself in between iterations. 
        If the <em>Hypothesis</em> EQ oracle is used, the shortest separating word between the learned and the given model is used as counterexample.
        This however presumes that the alphabets of both models are equal.
    </dd>
    <dt>Steps to learn</dt>
    <dd>
        You can also define how many hypotheses should be generated at maximum. When the learner stops, you can still continue
        learning from this point.
    </dd>
    <dt>Comment</dt>
    <dd>
        Furthermore a comment can be added that makes it easier to identify a specific learn result between others. The
        comment is a string value with a maximum amount of 120 characters.
    </dd>
    <dt>Web driver</dt>
    <dd>
        Choose a web browser where the tests should be run in. 
        Per default, the headless HTML Unit Driver is selected. 
        For all others, make sure you have it installed, otherwise the application might crash.
        Also, define the size of the browser window. 
        If the height and width are set to <em>0px</em>, the web drivers predefined dimensions are used.
    </dd>
    <dt>Membership query cache</dt>
    <dd>
        If membership queries should be cached. Enabling this option often reduces the total learning time.
    </dd>
</dl>

The mentioned equivalence oracles have different strategies on how to find counterexamples. 
Each one can be configured with different parameters that define their behaviour.

<dl>
    <dt>Random Word</dt>
    <dd>
        The Random Word oracle approximates equivalence queries by generating random words from learned symbol set and
        executes them on the learned applications. The oracles expects three parameters: <em>minLength</em> defines the
        minimum length of a generated word, <em>maxLength</em> the maximum length and <em>numberOfWords</em> the amount
        of randomly generated words to test.
    </dd>
    <dt>Complete</dt>
    <dd>
        This oracle generates all possible word within some limits. <em>minDepth</em> describes the minimum length of
        a generated word, <em>maxDepth</em> the maximum length.
    </dd>
    <dt>Sample</dt>
    <dd>
        If this oracle is chosen, counterexamples are searched by hand by the user.
    </dd>
    <dt>W-Method</dt>
    <dd>
        Uses the W-Method introduced in "Testing software design modeled by finite state machines" by T.S. Chow.
        This method can be restricted with a maximum depth.
    </dd>
    <dt>Hypothesis</dt>
    <dd>
        Uses an ideal model of an application to search for separating words and uses them as counterexamples.
        Note that the input alphabets should be the same.
    </dd>
</dl>  

In order to simplify the modeling phase, only the alphabet and the reset symbol has to be chosen. 
As default, the TTT algorithm is selected in combination with the random word oracle. 
A click on the button with the label "start" starts the learning process with the given configuration. 
The user gets redirected to a loading screen where the generated hypothesis is displayed as soon as the server generated one.

<div class="alert alert-info">
    The usage of the TTT algorithm is generally preferred from a users view as it outperforms the other algorithms in
    terms of speed. Furthermore, the default eq oracle configuration should be adjusted as it is chosen like it is for
    very small applications.
</div>

While ALEX is learning there are some restrictions concerning the functionality. 
You can not delete the current project as the instance is required by the learner. 
Due to the architecture of ALEX, there can always only be one learning process at a time.

While the learning screen is shown, the number of already executed MQs and the time that has passed since the learn
process started are displayed.

<div class="alert alert-warning">
    If you encounter that the number of executed MQs has not changed in a long time, it might be, depending on the size
    of your alphabet, that the learner hung up. Please have a look at the terminal output. If it is not active, a
    forced restart of ALEX is necessary.
</div>


## Hypothesis Interaction

The possibility to interact with generated models can be separated into two phases. The first one is while a learn
process is running and the second one is after having finished learning an applications. The latter is dealt with in the
section [Learning Experiment Analysis <b class="caret"></b>](#Learning_Experiment_Analysis "Learning Experiment Analysis").

Hypotheses are displayed as Mealy Machines and represent the learned behaviour of the tested application. Nodes are
labeled from 0 to n. The green node represents the initial state. Depending on the failed or successful execution of a
symbol, edges are labeled as follows:

- &lt;abbreviation&gt;/OK
- &lt;abbreviation&gt;/FAILED(&lt;number&gt;)

The first one indicates that the symbol with the displayed abbreviation could be executed successfully. The second one
shows that the execution of the symbol has failed. The number gives a hint on what action resulted in the failure. The
expression *FAILED(1)* for example tells that the first action of the symbol has failed to execute correctly.


### Internal Data Structures

ALEX provides the visualisation of the *Observation Table* that is used by the *LStar* algorithm and the *Discrimination
Tree* from the equally named algorithm. Both are saved for each iteration the learner executes and can be displayed in
the same panel corresponding hypothesis is presented in. While observation tables can be exported as a CSV file,
discrimination trees can be downloaded in the SVG format.


### Testing Counterexamples

In between two iterations of a learn process it is possible to search and test counterexamples directly on the
displayed hypothesis and then start the next iteration with respect to entered counterexamples. This process follows
this patter:

1. Choose the equivalence oracle *Sample* from the widget in the sidebar
2. Create a word by clicking on the labels of the hypothesis
3. In the widget click on the button *"Test"*
4. A notification will tell whether the word is a counterexample or not
5. Click on the button *"Add"* to add the counterexample the list that is considered for refinement
6. Proceed with step 1 or resume the learn process

Note that while testing a word and it results in being a counterexample, its output labels are automatically switched
to the actual output sequence. The list of symbols can be arranged with drag and drop for quickly testing different
words. Once a counterexample has been added to the list, it can be edited by clicking on it.

The server assumes that all words given by a user for the refinement actually are counterexamples. If this is not the
case the learn process may fail and the application may have to be restarted by killing the running process.


## Learning Experiment Analysis

After having learned an application, their test results are available for an analysis. This can happen in two ways
that the next sections deals with.


### Hypotheses Comparison

The first point is the visual comparison of hypotheses in a single tab. The page for that can be equally separated into
multiple columns, where each column can present a hypothesis or an internal data structure. That way it is possible to
have a look at the internal data structure and the hypothesis at the same time. Another possibility is to compare two
or more hypotheses of several test runs. The only restriction is that results can only be compared to other results from
the same project.

![Comparison of hypotheses](assets/learning/hypotheses-comparison.jpg)

The image above presents an example of a comparison of the same test run.
By clicking on the grey area on the right of the display a new column is created.
By clicking on the red button in a panel, the column is removed and the size of the other columns.


### Statistics

On the statistics page a list of learn results is presented.
It is either possible to generate a bar chart of a selection of final results or an area chart.
This one not only includes the cumulated values, but displays all values of all iterations of each step of multiple learn processes.
Statistics can be generated for the following values:

* The number of conducted membership queries
* The number of conducted equivalence queries
* The number of called symbols
* The size of the input alphabet
* The time it took to finish the test or a single step

Each generated chart can be exported as a SVG file.
Furthermore, an export of these values in a CSV file for further research is possible, too.
The image below shows an example of a chart of multiple final results (left) and one from multiple complete results (right).
In the second case the visibility of single tests can be toggled by clicking on the legend entry with the corresponding test number.

![Charts](assets/learning/charts.jpg)

Another aspect to mention is that statistics are generated on the fly and are bound to a learn result.
In case the learn result is deleted, the statistics can not be shown any longer.
