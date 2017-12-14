# Symbol Management

## Symbol Groups

With symbol groups symbols of a project can be organized. For example you can create a group that contains symbols that
are used to reset an application, another one for web related symbols and a further one for symbols relating to your
REST API, but it's completely up to you. **Do not mistake a symbol group for an alphabet!** A symbol group is just a
container for symbols and should help you organize them.

Symbol groups can be created, edited and deleted under the URL */symbols*. In order to create a new one, hover over the
*create* button in the sub-navigation and click on the corresponding entry. In the modal window choose a unique name
for a symbol group. In order to change the name of a group or to delete it, click on the gear-button beside the symbol
group name and do these actions in the modal window.

ALEX offers a default group where new symbols are moved to if no other group is specified. It can not be deleted, but
renamed. Furthermore, deleting a symbol group induces all symbol to get deleted as well (more in the next chapter).

For a better overview over a large set of symbols, symbol groups can be collapsed by clicking on the arrow button on
the left of each entry.


## Symbols

Symbols can be created, updated, deleted and moved into other symbol groups. 
The creation of a symbol requires several properties.

| Name          | Description                                                                                       |
|---------------|---------------------------------------------------------------------------------------------------|
| name          | A unique name of the symbol                                                                       |
| symbol group  | Optionally the symbol group the symbol should be created in can be specified. If this property is not given, the symbol is moved to the default group |

Note that once you *delete* a symbol it is not permanently removed from the server, but hidden. As a consequence, they
can be made visible by going to */symbols/trash* or by clicking the corresponding menu entry in the sidebar. There, a
list of all hidden symbols is given. Recovering a symbol makes it appear again in the group it previously was in. In
case the group has been deleted, the symbol is moved to the default group.


### Actions

The functionality of a symbol is defined by its actions and their execution order. An action can be understood as a real
interaction with a system, like clicking on buttons, submitting forms, making requests to an API and so on.

In order to manage actions of a single symbol, go to */symbols/\<symbolId\>/actions* or click on the link below a symbol
under */symbols*. Here, actions can be created, edited, deleted and ordered. The list that is shown represents the order
of execution on the application as soon as the symbol is called by the learner. Make sure to save changes that have been
made in the current session, since CRUD operations and re-ordering of actions is not saved automatically. This is simply
possible by clicking the *save* button on the right in the sub-menu.

The creation of actions is realized in a modal window that shows the action editor as shown in the picture above. As one
can see, the left column contains dropdown boxes with a logical grouping of actions. The right column reveals a form
with required input fields for a selected action. The action groups are presented in to following.

Each action can be marked with three different flags which are

| Flag          | Description                                                                                           |
|---------------|-------------------------------------------------------------------------------------------------------|
| negated       | Negates the outcome of an action.                                                                     |
| ignoreFailure | If this flag is set to "true" proceedings actions are executed although the action failed             |
| disabled      | If this flag is set to "true" its execution is skipped during the call of its symbol                  |


#### Web Actions

Web actions are used to interact with a browser interface as a real person would do. They are based on Selenium actions
and ALEX offers a subset of these that are presented in the table below.

| Name                   | Description                                                                  |
|------------------------|------------------------------------------------------------------------------|
| Alert - Accept/Dismiss | Accepts or dismisses an alert dialog.                                        |
| Alert - Get Text       | Save the displayed text of an alert/confirm/prompt window in a variable.     |
| Alert - Send keys      | Send user input to a prompt alert.                                           |
| Check Attribute        | Checks the value of an attribute of an element                               |
| Check Node             | Check if a certain element is present on the website.                        |
| Check Text             | Check if a certain text is part of the website body.                         |
| Check Title            | Checks if the page title is a certain string.                                |
| Clear                  | Clear an input field.                                                        |
| Click                  | Click or double click on an element.                                         |
| Click Link By Text     | Click on a link with a specific text value.                                  |
| Execute JavaScript     | Execute a JavaScript snipped in the page.                                    |
| Fill                   | Clear and fill an input field with some text.                                |
| Open URL               | Request a specific site.                                                     |
| Move Mouse             | Move the cursor to a specific element or coordinates                         |
| Press Key              | Press a special key on the keyboard                                          |
| Submit                 | Submit a form.                                                               |
| Select                 | Select an option form an select input field.                                 |
| Switch to              | Switch back to the parent content or the default content from another frame. |
| Switch to Frame        | Switch the Selenium context to another frame.                                |
| Wait for Attribute     | Wait until an attribute of an element has or contains a specific value.      |
| Wait for Text          | Wait until a specific text is visible on the website.                        |
| Wait for Title         | Wait until the title of a page changes.                                      |
| Wait for Node          | Wait until the state of an element changes.                                  |

More detailed information about all the parameters of each web action is omitted as this point, since the forms in the
front-end should be labeled sufficiently.

If you play around a little with the action editor, you may realize that most web actions require you to enter a CSS or
and XPath selector to an element. This may be not that easy to find out in case you are not very familiar with 
HTML. So, there is a button that is labeled with *"Element Picker"*. This is a special feature of ALEX for selecting 
HTML elements from your website directly without having to know HTML. Note that this only extracts CSS selectors.


##### HTML Element Picker

The HTML element picker has been tested but it can not be ensured that picking the right element works in all possible
use cases. If the HTML picker should fail you can still get the CSS path of an element with the developer tools of your
browser.

In Google Chrome, make a right click on the desired element and choose the entry *Inspect Element*. The developer tools
sidebar should open and reveal the element in the DOM tree. There, make another right click on the element and choose
*Copy CSS Path* which copies the unique CSS selector in the clipboard of your operating system. A similar approach can
be applied with Firefox.

1. Load a URL by entering a path relative to the base URL of the project in the URL bar on the top left.
2. Click on the button with the wand symbol. All elements should now get a thick red border as soon as you hover over it.
3. Click on the element you need the CSS path from. The selection mode should be disabled.
4. Click on the button with the label "ok" on the top right. The CSS path should automatically be entered in the correct
input element.

In the case that clicking on an element does not work as indented, probably because of some JavaScript, pressing the
"Ctrl" button on the keyboard has the same effect as a click, but does not fire a click event.

For some actions, like "CheckText", the HTML element picker can be used as well. With the same method, it automatically
extracts the textContent value of an element and pastes the content to the corresponding input field. This also works
with the "Fill" action. Enter a value in the desired input field, select the element with the HTML element picker and it
extracts both, the value and the CSS path of it.

##### Action Recorder


#### REST Actions

REST actions are the counterpart to web actions. They are used to communicate with RESTful interfaces. The table below
shows a list of available actions.

| Name                   | Description                                                     |
|------------------------|-----------------------------------------------------------------|
| Call                   | Make a REST Call.                                                 |
| Check Attribute Exists | Check if the response has an specific attribute.                |
| Check Attribute Type   | Check if an attribute in the response has a specific type.      |
| Check Attribute Value  | Check if an attribute in the response has a specific value.     |
| Check Header Field     | Check if the response has a certain header field.               |
| Check Status           | Check if a previous response returned the expected HTTP status. |
| Check Text             | Check the response body.                                        |
| Validate JSON          | Validate the body of a response against a JSON schema.          |

Keep in mind that working with HTTP requests and responses follows a certain pattern. Normally, you make a request and
analyze the results. The order of your REST actions should also look like that. Start with a *Call* action and use other
actions to work with the response.


#### General Actions

Actions of this group allow the interaction between different symbols and actions, for example by storing and passing
String and Integer values to other actions.

| Name                           | Description                                                         |
|--------------------------------|---------------------------------------------------------------------|
| Assert Counter                 | Asserts the value of a counter.                                     |
| Assert Variable                | Asserts the value of a variable.                                    |
| Increment Counter              | Increment a counter by a given value.                               |
| Execute Symbol                 | Include and execute another symbol.                                 |
| Set Counter                    | Set a counter to a new value.                                       |
| Set Variable                   | Set a variable to a new value.                                      |
| Set Variable by Cookie         | Set a variable to the value of a cookie                             |
| Set Variable by HTML Element   | Set a variable to a value form a website element.                   |
| Set Variable by JSON Attribute | Set a variable to a value form a JSON response.                     |
| Set Variable by Node Attribute | Set a variable to the value of an attribute of an element.          |
| Set Variable by Node Count     | Set a variable to the number of elements matching a selector.       |
| Set Variable by Regex Group    | Set a variable to a group in a regex match.                         |
| Wait                           | Wait for a specific amount of time.                                 |

<div class="alert alert-info">
    The wait action can be useful for background tasks or AJAX calls, but should be used with caution because it can slow
    down the learn process.
</div>


##### Working with Counters, Variables and Files

Many web applications handle dynamic data and allow file uploads. In order to model and learn such behaviors and to
allow interaction between different symbols, actions and learn processes, *counters*, *variables* and *files* are
introduced.

<dl>
    <dt>Counter</dt>
    <dd>
        As the name indicates counters are integer values that are persisted in the database per project. They
        can be incremented and set at will. Commonly they are used to create multiple objects of the same kind. Counters
        can help to model a system reset and thereby allow a consecutive execution of multiple learn processes without
        having to manually reset the application in between every test.
    </dd>
    <dt>Variables</dt>
    <dd>
        Variables can only contain String values and are kept alive for a single membership query.
    </dd>
    <dt>Files</dt>
    <dd>
        In order to learn websites that allow its users to upload files, this feature can be used as well. Make sure the
        file that should be uploaded for learn purposes has been imported to the project of the website.
    </dd>
</dl>

In order to make use of those in actions, there is a notation that has to be used in action fields, as presented in the
following table.

| Notation                 | Description                                                           |
|--------------------------|-----------------------------------------------------------------------|
| \{\{#counterName\}\}      | The value of the counter with the name *counterName* is inserted      |
| \{\{\$variableName\}\}   | The value of the variable with the name *variableName* is inserted    |
| \{\{\\filename.ext\}\}   | The absolute path of the file *filename.ext* is inserted              |

As an example for the use of counters and variables, let there be a variable *userName* with the value \'*Admin*\' and
a counter *countLogins* with the value \'*5*\'. The following symbol makes use of both.

```json
[{
    "name" : "symbol1",
    "actions" : [{
        "type" : "web_checkForText",
        "value" : "Hello {{$userName}}! You logged in for the {{countLogins}}th time.",
        "regexp" : false
    }]
}, ... ]
```

As soon as the action is executed the value of the property \'value\' is parsed, the values for the variable *userName*
and the counter *countLogins* are inserted into the String. The resulting text that is searched for would be \"*Hello
Admin! You logged in for the 5th time today.*\".

Normally, files are only applied to input fields with the type *\"file\"*, but do as you please. Given the following
element on a page

```html
<input id="fileInput" type="file" />
```

The action would look like this:

```json
[{
    ...
    "actions" : [{
        "type" : "web_fill",
        "node" : {
            "selector" : "#fileInput",
            "type" : "CSS" 
        },
        "value" : "{{\filename.ext}}"
    }]
}, ... ]
```

<div class="alert alert-warning">
    Be aware that currently, due to the limitations of the used Selenium webdriver, the upload of files only works if
    a learn process is executed with enabled javascript, which is enabled by default.
</div>


### Export & Import

If you want to save a set of symbols for another project or use already existing ones, the export and import function
might be of interest for you. Note that when exporting symbols, their id and group are deleted in order to be
compatible with other projects.

ALEX supports the import from and export in a JSON file. Technically, the possibility of creating symbols with a simple
text editor by hand and uploading them to the system is given. Therefore, the file has to look as follows:

```json
[{
    "name" : "symbol1",
    "actions" : [ ... ]
}, ... ]
```