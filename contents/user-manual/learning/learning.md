# Learning

Models are represented as Mealy Machines and represent the learned behaviour of the tested application. 
Nodes are labeled from *0* to *n*, where nodes represent the internal states of the target application and state *0* (visualised by a green node) is the initial state. 
Edges denote transitions from one state to another where the edge labels show the symbols whose execution led to the transition into another state.
Edge labels have the following pattern:

- &lt;symbolName&gt; / Ok
- &lt;symbolName&gt; / Failed (&lt;number&gt;)

where &lt;symbolName&gt; is the name of the symbol and the text after the \"/\" displays the output of the system.
In ALEX, the output of the system is interpreted as *"Ok"*, if all actions of a symbol have been executed successfully.
On the other hand *"Failed (n)"* means that the execution failed on the *n*-th action of the symbol.


## Resuming a Previous Learning Process

The learning process usually takes a lot of time when learning models from web applications.
The more annoying it is if the learning process is interrupted due to various reasons and you have to start learning from the beginning.
Luckily, there is the possibility to resume an old process from an intermediate model.

![Resuming 1](assets/learning/resuming-1.jpg)

In the results overview, expand the dropdown menu on the corresponding result and click on the item labeled by *Continue learning* <span class="label">1</span>.

![Resuming 2](assets/learning/resuming-2.jpg)

You are being redirected to the view you should be familiar with from the learning process.
Here, simply select the step <span class="label">2</span> you want to continue learning from and configure the equivalence oracle according to your needs.
Finally, click on <span class="label">3</span> to resume the learning process.

<div class="alert alert-info">
    When resuming a learning process and using the random equivalence oracle, make sure you use a different seed that in the run before.
    Otherwhise, membership queries are posed that have been posed before, which is not effective.
</div>

![Resuming 3](assets/learning/resuming-3.jpg)

You can even add additional input symbols that should be included in the next iteration of the learning process by selecting them in <span class="label">4</span>.


## Restrictions While Learning

While ALEX is learning there are some restrictions concerning the functionality:

1. You can not delete the current project as the instance is required by the learner. 
2. Due to the architecture of ALEX, there can always only be one learning process at a time per project.