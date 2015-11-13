import {_} from '../../libraries';
import {events} from '../../constants';
import {Symbol} from '../../entities/Symbol';

/**
 * The controller that handles CRUD operations on symbols and symbol groups.
 *
 * @param $scope - angular scope object
 * @param SessionService - The SessionService
 * @param SymbolResource - The Symbol API Resource handler
 * @param SymbolGroupResource - The SymbolGroup factory
 * @param ToastService - The ToastService
 * @param FileDownloadService - The FileDownloadService
 * @param EventBus
 * @constructor
 */
// @ngInject
function SymbolsController($scope, SessionService, SymbolResource, SymbolGroupResource, ToastService,
                           FileDownloadService, EventBus) {

    /**
     * The project that is saved in the session
     * @type {Project}
     */
    $scope.project = SessionService.project.get();

    /**
     * indicates if symbol groups are displayed collapsed
     * @type {boolean}
     */
    $scope.groupsCollapsed = false;

    /**
     * The model for selected symbols
     * @type {Symbol[]}
     */
    $scope.selectedSymbols = [];

    /**
     * The symbol groups that belong to the project
     * @type {SymbolGroup[]}
     */
    $scope.groups = [];

    // fetch all symbol groups and include all symbols
    SymbolGroupResource.getAll($scope.project.id, {embedSymbols: true})
        .then(groups => {
            $scope.groups = groups;
        });

    // listen on group create event
    EventBus.on(events.GROUP_CREATED, (evt, data) => {
        $scope.addGroup(data.group);
    }, $scope);

    // listen on group update event
    EventBus.on(events.GROUP_UPDATED, (evt, data) => {
        $scope.updateGroup(data.group);
    }, $scope);

    // listen on group delete event
    EventBus.on(events.GROUP_DELETED, (evt, data) => {
        $scope.deleteGroup(data.group);
    }, $scope);

    // listen on symbol created event
    EventBus.on(events.SYMBOL_CREATED, (evt, data) => {
        $scope.addSymbol(data.symbol);
    }, $scope);

    // listen on symbol update event
    EventBus.on(events.SYMBOL_UPDATED, (evt, data) => {
        $scope.updateSymbol(data.symbol);
    }, $scope);

    /**
     * Finds the symbol group object from a given symbol. Returns undefined if no symbol group was found.
     *
     * @param symbol - The symbol whose group object should be found
     * @returns {SymbolGroup|undefined} - The found symbol group or undefined
     */
    function findGroupFromSymbol(symbol) {
        return _.find($scope.groups, {id: symbol.group});
    }

    /**
     * Extracts all symbols from all symbol groups and merges them into a single array
     *
     * @returns {Symbol[]}
     */
    $scope.getAllSymbols = function () {
        return _.flatten(_.pluck($scope.groups, 'symbols'));
    };

    /**
     * Adds a single new symbol to the scope by finding its corresponding group and adding it there
     *
     * @param {Symbol} symbol - The symbol that should be added
     */
    $scope.addSymbol = function (symbol) {
        findGroupFromSymbol(symbol).symbols.push(symbol)
    };

    /**
     * Removes a list of symbols from the scope by finding the group of each symbol and removing it from
     * it
     *
     * @param {Symbol[]} symbols - The symbols that should be removed
     */
    $scope.removeSymbols = function (symbols) {
        var group;
        _.forEach(symbols, function (symbol) {
            group = findGroupFromSymbol(symbol);
            _.remove(group.symbols, {id: symbol.id});
        })
    };

    /**
     * Updates an existing symbol
     *
     * @param {Symbol} updatedSymbol - The updated symbol object
     */
    $scope.updateSymbol = function (updatedSymbol) {
        $scope.updateSymbols([updatedSymbol]);
    };

    /**
     * Updates multiple existing symbols
     *
     * @param {Symbol[]} updatedSymbols - The updated symbol objects
     */
    $scope.updateSymbols = function (updatedSymbols) {
        var group;
        var i;
        _.forEach(updatedSymbols, function (symbol) {
            group = findGroupFromSymbol(symbol);
            i = _.findIndex(group.symbols, {id: symbol.id});
            if (i > -1) {
                group.symbols[i].name = symbol.name;
                group.symbols[i].abbreviation = symbol.abbreviation;
                group.symbols[i].group = symbol.group;
                group.symbols[i].revision = symbol.revision;
            }
        })
    };

    /**
     * Moves a list of existing symbols into another group.
     *
     * @param {Symbol[]} symbols - The symbols that should be moved
     * @param {SymbolGroup} group - The group the symbols should be moved into
     */
    $scope.moveSymbolsToGroup = function (symbols, group) {
        var grp = _.find($scope.groups, {id: group.id});

        _.forEach(symbols, function (symbol) {
            var g = _.find($scope.groups, {id: symbol.group});
            var i = _.findIndex(g.symbols, {id: symbol.id});
            g.symbols.splice(i, 1);
            symbol.group = group.id;
            grp.symbols.push(symbol);
        })
    };

    /**
     * Deletes a single symbol from the server and from the scope if the deletion was successful
     *
     * @param {Symbol} symbol - The symbol to be deleted
     */
    $scope.deleteSymbol = function (symbol) {
        SymbolResource.delete(symbol)
            .success(function () {
                ToastService.success('Symbol <strong>' + symbol.name + '</strong> deleted');
                $scope.removeSymbols([symbol]);
            })
            .catch(function (response) {
                ToastService.danger('<p><strong>Deleting symbol failed</strong></p>' + response.data.message);
            })
    };

    /**
     * Deletes all symbols that the user selected from the server and the scope, if the deletion was successful
     */
    $scope.deleteSelectedSymbols = function () {
        SymbolResource.delete($scope.selectedSymbols)
            .success(function () {
                ToastService.success('Symbols deleted');
                $scope.removeSymbols($scope.selectedSymbols);
            })
            .catch(function (response) {
                ToastService.danger('<p><strong>Deleting symbols failed</strong></p>' + response.data.message);
            })
    };

    /**
     * Adds a new symbol group to the scope
     *
     * @param {SymbolGroup} group - The group that should be added
     */
    $scope.addGroup = function (group) {
        $scope.groups.push(group);
    };

    /**
     * Updates a symbol group in the scope by changing its name property to the one of the groups that is passed
     * as a parameter
     *
     * @param {SymbolGroup} updatedGroup - The updated symbol group
     */
    $scope.updateGroup = function (updatedGroup) {
        _.find($scope.groups, {id: updatedGroup.id}).name = updatedGroup.name;
    };

    /**
     * Removes a symbol group from the scope and also removes its symbols
     *
     * @param {SymbolGroup} group
     */
    $scope.deleteGroup = function (group) {
        $scope.removeSymbols(group.symbols);
        _.remove($scope.groups, {id: group.id});
    };

    /**
     * Collapses all groups or expands them
     */
    $scope.toggleCollapseAllGroups = function () {
        $scope.groupsCollapsed = !$scope.groupsCollapsed;
        for (var i = 0; i < $scope.groups.length; i++) {
            $scope.groups[i]._collapsed = $scope.groupsCollapsed;
        }
    };

    /**
     * Deletes all properties that are not needed for downloading symbols which are the id, revision, project, group
     * and hidden properties. They are removed so that they can later be uploaded and created like new symbols.
     */
    $scope.exportSelectedSymbols = function () {
        if ($scope.selectedSymbols.length > 0) {
            var symbols = _(angular.copy($scope.selectedSymbols))
                .sortBy(function (symbol) {
                    return symbol.id;
                }).value();

            _.forEach(symbols, function (symbol) {
                _.forEach(symbol.actions, function (action) {
                    if (action.type === 'executeSymbol') {
                        action.symbolToExecute.revision = 1;
                        _.forEach(symbols, function (s, j) {
                            if (s.id === action.symbolToExecute.id) {
                                action.symbolToExecute.id = j + 1;
                            }
                        })
                    }
                });
                delete symbol._selected;
                delete symbol._collapsed;
                delete symbol.revision;
                delete symbol.project;
                delete symbol.group;
                delete symbol.hidden;
                delete symbol.id;
            });

            FileDownloadService.downloadJson(symbols)
                .then(function () {
                    ToastService.success('Symbols exported')
                })
        } else {
            ToastService.info('Select symbols you want to export')
        }
    }
}

export default SymbolsController;