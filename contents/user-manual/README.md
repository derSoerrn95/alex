# User Manual

Here and in the following sections, we present a detailed explanation of the concepts and ways to use ALEX.
If you find bugs of any kind relating this application or inaccuracies in this manual, [let us][mails] know.

[mails]: mailto:alexander.bainczyk@tu-dortmund.de,alexander.schieweck@tu-dortmund.de


## Description and Features

ALEX offers a simplicity-oriented way to model and execute learning experiments for web applications and web services using active automata learning. 
Based on the function set the [LearnLib][learnlib] and inspired by the [LearnLib Studio][learnlibStudio], ALEX lays a focus on the ease to use of the tool while offering an extensive feature set:

* Inferring Mealy machines of web applications and web services using active automata learning techniques
* Graphical symbol and learning process modelling
* Automatic generation and visualization of
    * Models
    * Algorithmic data structures (observation table and discrimination tree)
    * Statistics of learning experiments
* Simultaneous learning of web applications and web services
* Various learning algorithms and equivalence approximation strategies
* Integration testing capabilities
* And many more...


## Terminology

This document may contain some terms related to automata learning. 
We now want to explain often used terms that may be helpful for understanding this document.

<dl>
    <dt>Learner</dt>
    <dd>A learner infers an automaton model of an application by posing test inputs to the system and analyzing its outputs.</dd>
    <dt>Membership Query (MQ)</dt>
    <dd>The test inputs the learner poses to the system are called membership queries.</dd>
    <dt>Equivalence Query (EQ)</dt>
    <dd>An equivalence query is posed by an equivalence oracle. It checks whether the learned automaton represents the 
    behavior of the tested application correctly.</dd>
</dl>


## Working Objects

<dl>
    <dt>User</dt>
    <dd>A user is identified by its email address and can have one of two roles: <em>admin</em> or <em>registered</em>.</dd>
    <dt>Project</dt>
    <dd>A project is the main object that the following objects belong to. It is bound to a unique name and a URL that
    starts with *"http\[s\]://"*. This property defines the base URL of the application under testing. In ALEX it is
    allowed to create and manage multiple projects, thus, for example, allowing to treat a web application and a web
    service as different projects or managing multiple complete different applications. A project can have multiple URLs
    where *the same* application can be accessed to make use of parallelisation techniques.</dd>
    <dt>Symbol Group</dt>
    <dd>Each project has a list of symbol groups. Symbol groups are logical containers for symbols. They allow grouping 
    symbols, e.g. by their purpose or by their feature. They are defined by a unique name. For every project, there is 
    a default group with the name <em>"Default Group"</em> which can not be deleted.</dd>
    <dt>Symbol</dt>
    <dd>Symbols are used by the learner to learn an application. They are defined by a unique name which is displayed 
    on an inferred model as the label of an edge. Each symbol consists of a sequence of actions that define the actual 
    logic of the symbol once it is executed by the learner.</dd>
    <dt>Action</dt>
    <dd>Actions are atomic operations on an application. In ALEX, there are three types. <strong>Web</strong> actions are
    inspired by Selenium and directly interact with the web interface of an application that is for example clicking a
    button, sending user input to input fields and submitting web forms. <strong>REST</strong> actions allow to define 
    interactions with a REST APIs and <strong>General</strong> actions allow interoperability between actions and 
    symbols.</dd>
    <dt>Learner Configuration</dt>
    <dd>For each learning process, a learner configuration has to be created. It consists of an alphabet, which is a set
    of symbols, a reset symbol (a symbol that is used to reset an application before each MQ), a learning
    algorithm, an equivalence oracle and some other parameters. Of cause the length the alphabet has to contain at 
    least one symbol and reset symbol is required as well.</dd>
    <dt>Learner Result</dt>
    <dd>As soon as a learning process has finished, a learner result is generated for each step the learner took to generate
    the model. For each step, it contains the actual learner configuration, some statistics and the hypothesis as a JSON 
    representation.</dd>
    <dt>Test Case</dt>
    <dd>A test case can be understood as a single integration test. It consists of sequence of symbols that are executed
    on the system. The execution of a test case either fails or succeeds.</dd>
    <dt>Test Suite</dt>
    <dd>Multiple test cases can be bundles into a test suite. If a test suite is executed, all of its test cases are
    executed. The execution succeeds, if all test cases succeeds, otherwise it fails. Test suites can be nested.</dd>
</dl>


## Workflow

When working with ALEX, the workflow for learning a web application starts with the creation of a project and results
in the automatic generation of a model of this application. The next sections of this user manual deal with an in-depth 
view on the points listed below:

1. Create a project with the root URL where the target application can be accessed
2. Model the input alphabet (a set of symbols), including one that handles the reset logic
3. Setup and run a learning process
4. Display the learned model, algorithmic data structures and analyze the statistics


## Frontend

Starting from the entry URL of ALEX, the graphical client can be accessed under http://localhost:&lt;port&rt;. 
From there on, the following URLs lead to different aspects of the application.

| URL                                 | Description                                               |
|-------------------------------------|-----------------------------------------------------------|
| /about                              | An information page about the application                 |
| /counters                           | Lists and manages the counters of a project               |
| /error                              | Shows fatal error messages                                |
| /files                              | Lists and manages uploaded files to a project             |
| /help                               | A page that lists information about ALEX                  |
| /home                               | The home screen to login and create new users             |
| /learner/setup                      | Setup and start a learning experiment                     |
| /learner/start                      | Shows intermediate learner results                        |
| /projects                           | Shows a list of all projects of a user                    |
| /projects/dashboard                 | Shows the dashboard of the opened project                 |
| /symbols                            | Create, update & delete symbol groups and symbols         |
| /symbols/&lt;symbolId&gt;/actions   | Manage actions of a specific symbol                       |
| /symbols/&lt;symbolId&gt;/history   | Restore old revisions of a specific symbol                |
| /symbols/trash                      | Restore deleted symbols                                   |
| /symbols/import                     | Import symbols from a \*.json file                        |
| /results                            | Lists all finished final learning results of a project    |
| /results/&lt;testNos&gt;/compare    | Show the hypotheses of the processes with <testNos>       |
| /settings                           | Specify web drivers                                       |
| /statistics                         | Show a list of learner results and choose some for stats  |
| /statistics/&lt;testNos&gt;/compare | Show statistics for learner results with <testNos>        |
| /tests                              | Management of test suites and test cases                  |
| /tests/&lt;id&gt;                   | Editing the test case with a given id                     |

*Except for the "about", "help", "error" and the "home" page, all routes require that a user is logged in and a project has been created and is opened.*

[learnlib]: https://learnlib.de/
[learnlibStudio]: http://ls5-www.cs.tu-dortmund.de/projects/learnlib/download.php