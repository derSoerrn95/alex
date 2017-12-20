# Learning Process Setup

In order to start learning an application, a learn process has to be modeled. 
Such a process always consists of the following components:

* An input alphabet (set of symbols)
* A symbol to reset the application
* A learning algorithm
* A parametrized equivalence oracle
* A maximum amount of steps to learn
* A specification of a web browser

## Configuration

- modal window

<div class="alert alert-info">
    The usage of the TTT algorithm is generally preferred from a users view as it outperforms the other algorithms in terms of speed. 
    Furthermore, the default eq oracle configuration should be adjusted as it is chosen like it is for very small applications.
</div>

<dl>
    <dt>Random Word</dt>
    <dd>
        The <em>Random Word</em> EQ oracle approximates an equivalence query by generating random words from the input alphabet and executes them on the system. 
        The oracles expects three parameters: <em>minLength (> 0)</em> defines the minimum length of a generated word, <em>maxLength (>= minLength)</em> the maximum length and <em>numberOfWords (> 0)</em> the amount of randomly generated words to test.
    </dd>
    <dt>Complete</dt>
    <dd>
        The <em>Complete</em> EQ oracle generates all possible words from an alphabet within some limits. 
        <em>minDepth (> 0)</em> describes the minimum length of word, <em>maxDepth (>= minDepth)</em> the maximum length.
    </dd>
    <dt>W-Method</dt>
        <dd>
            The <em>W-Method</em> EQ oracle generates words based on a transition coverage of the graph under the assumption of <em>maxDepth</em> additional states.
        </dd>
    <dt>Sample</dt>
    <dd>
        If this oracle is chosen, counterexamples are searched and specified manually by the user.
    </dd>
    <dt>Hypothesis</dt>
    <dd>
        Uses an ideal model of an application to search for differences and uses them as counterexamples.
        Note that the input alphabets should be the same.
    </dd>
</dl>  