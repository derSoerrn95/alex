# Symbol Management

## Symbol Groups

## Symbols

The creation of a symbol requires two properties:

| Name          | Description                                                                                       |
|---------------|---------------------------------------------------------------------------------------------------|
| name          | A unique name of the symbol                                                                       |
| symbol group  | The group in which the symbol is put. Per default, the default group of the project is used.      |


### Export & Import

If you want to save a set of symbols for another project or use existing ones, ALEX offers an export and import feature.
Note that existing symbol groups are not exported in order to be compatible with other projects.

![Export](assets/export-1.jpg)

In order to export symbols, select the corresponding symbols in the overview and click on the export button <span class="label">1</span>.
You are then asked for name of the JSON file which will be downloaded.

![Import 1](assets/import-1.jpg)

In the same view, you can import existing symbols from a JSON file by clicking on <span class="label">2</span> which opens a modal window.

![Import 2](assets/import-2.jpg)

Here, drag and drop the JSON file into the field <span class="label">3</span> and the symbols will be displayed below.
The import will not work unless the names of the symbols are unique within the project.
So, edit the symbol names <span class="label">4</span> or delete the ones you do not need.
Finally, confirm your selection by clicking on <span class="label">5</span>.
If everything goes fine, the modal window will close automatically and the symbols appear in the default group.