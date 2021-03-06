/*
 * Copyright 2018 TU Dortmund
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

import remove from 'lodash/remove';
import uniqueId from 'lodash/uniqueId';
import {AlphabetSymbol} from '../../../entities/alphabet-symbol';
import {Selectable} from '../../../utils/selectable';

/**
 * The controller that handles the page for managing all actions of a symbol. The symbol whose actions should be
 * manages has to be defined in the url by its id.
 */
class SymbolViewComponent {

    /**
     * Constructor.
     *
     * @param $scope
     * @param $stateParams
     * @param {SymbolResource} SymbolResource
     * @param {SessionService} SessionService
     * @param {ToastService} ToastService
     * @param {ActionService} ActionService
     * @param {ClipboardService} ClipboardService
     * @param $state
     * @param {ActionRecorderService} ActionRecorderService
     * @param dragulaService
     * @param $uibModal
     */
    // @ngInject
    constructor($scope, $stateParams, SymbolResource, SessionService, ToastService, ActionService, ClipboardService,
                $state, dragulaService, ActionRecorderService, $uibModal) {
        this.SymbolResource = SymbolResource;
        this.ToastService = ToastService;
        this.ActionService = ActionService;
        this.ClipboardService = ClipboardService;
        this.ActionRecorderService = ActionRecorderService;
        this.$uibModal = $uibModal;

        /**
         * The project that is stored in the session.
         * @type {Project}
         */
        this.project = SessionService.getProject();

        /**
         * The symbol whose actions are managed.
         * @type {AlphabetSymbol|null}
         */
        this.symbol = null;

        /**
         * The selected actions.
         * @type {Selectable}
         */
        this.selectedActions = null;

        /**
         * Whether there are unsaved changes to the symbol.
         * @type {boolean}
         */
        this.hasChanged = false;

        // load all actions from the symbol
        // redirect to an error page when the symbol from the url id cannot be found
        this.SymbolResource.get(this.project.id, $stateParams.symbolId)
            .then(symbol => {

                // create unique ids for actions so that they can be found
                symbol.actions.forEach(action => action._id = uniqueId());

                // add symbol to scope and create a copy in order to revert changes
                this.symbol = symbol;
                this.selectedActions = new Selectable(this.symbol.actions, '_id');
            })
            .catch(() => {
                $state.go('error', {message: `The symbol with the ID "${$stateParams.symbolId}" could not be found`});
            });

        const keyDownHandler = (e) => {
            if (e.ctrlKey && e.which === 83) {
                e.preventDefault();
                this.saveChanges();
                return false;
            }
        };

        document.addEventListener('keydown', keyDownHandler);

        // dragula
        dragulaService.options($scope, 'actionList', {
            removeOnSpill: false,
            mirrorContainer: document.createElement('div')
        });

        $scope.$on('actionList.drop-model', () => {
            this.hasChanged = true;
        });

        $scope.$on('$destroy', () => {
            dragulaService.destroy($scope, 'actionList');
            document.removeEventListener('keydown', keyDownHandler);
        });
    }

    /**
     * Deletes a list of actions.
     *
     * @param {Object[]} actions - The actions to be deleted.
     */
    deleteActions(actions) {
        if (actions.length > 0) {
            actions.forEach(action => {
                remove(this.symbol.actions, {_id: action._id});
            });
            this.selectedActions.unselectAll();
            this.ToastService.success('Action' + (actions.length > 1 ? 's' : '') + ' deleted');
            this.hasChanged = true;
        }
    }

    deleteSelectedActions() {
        this.deleteActions(this.selectedActions.getSelected());
    }

    editSelectedAction() {
        if (this.selectedActions.getSelected().length === 1) {
            const action = this.selectedActions.getSelected()[0];
            this.$uibModal.open({
                component: 'actionEditModal',
                resolve: {
                    modalData: () => {
                        const a = this.ActionService.create(JSON.parse(JSON.stringify(action)));
                        a._id = action._id;
                        return {action: a};
                    }
                }
            }).result.then(updatedAction => {
                this.updateAction(updatedAction);
            });
        }
    }

    /**
     * Adds a new action to the list of actions of the symbol and gives it a temporary unique id.
     *
     * @param {Action} action - The action to add.
     */
    addAction(action) {
        action._id = uniqueId();
        this.symbol.actions.push(action);
        this.ToastService.success('Action created');
        this.hasChanged = true;
    }

    /**
     * Adds new actions to the symbol.
     *
     * @param {Action[]} actions
     */
    addActions(actions) {
        actions.forEach(action => {
            this.symbol.actions.push(action);
        });
        this.ToastService.success('Actions added');
        this.hasChanged = true;
    }

    /**
     * Updates an existing action.
     *
     * @param {Object} updatedAction - The updated action.
     */
    updateAction(updatedAction) {
        const action = this.symbol.actions.find(a => a._id === updatedAction._id);
        for (let prop in action) {
            action[prop] = updatedAction[prop];
        }
        this.ToastService.success('Action updated');
        this.hasChanged = true;
    }

    /**
     * Saves the changes that were made to the symbol by updating it on the server.
     */
    saveChanges() {
        if (!this.hasChanged) {
            this.ToastService.info('There are no changes to save.');
            return;
        }

        // make a copy of the symbol
        const symbolToUpdate = new AlphabetSymbol(this.symbol);

        // update the symbol
        return this.SymbolResource.update(symbolToUpdate)
            .then(updatedSymbol => {
                this.ToastService.success('Symbol <strong>' + updatedSymbol.name + '</strong> updated');
                this.hasChanged = false;
            })
            .catch(response => {
                this.ToastService.danger('<p><strong>Error updating symbol</strong></p>' + response.data.message);
            });
    }

    /** Copies actions to the clipboard. */
    copySelectedActions() {
        const actions = this.selectedActions.getSelected();
        if (actions.length > 0) {
            this.ClipboardService.copy('actions', JSON.parse(JSON.stringify(actions)));
            this.ToastService.info(actions.length + ' actions copied to clipboard');
        }
    }

    copyAction(action) {
        this.ClipboardService.copy('actions', JSON.parse(JSON.stringify([action])));
        this.ToastService.info('The action has been copied to the clipboard.');
    }

    setChanged(changed) {
        this.hasChanged = changed;
    }

    /** Copies actions to the clipboard and removes them from the scope. */
    cutSelectedActions() {
        const actions = this.selectedActions.getSelected();
        if (actions.length > 0) {
            this.ClipboardService.cut('actions', JSON.parse(JSON.stringify(actions)));
            this.deleteActions(actions);
            this.ToastService.info(actions.length + ' actions cut to clipboard');
            this.hasChanged = true;
        }
    }

    cutAction(action) {
        this.ClipboardService.cut('actions', JSON.parse(JSON.stringify([action])));
        this.deleteActions([action]);
        this.ToastService.info('The action has been copied to the clipboard.');
    }

    /**
     * Pastes the actions from the clipboard to the end of of the action list.
     */
    pasteActions() {
        let actions = this.ClipboardService.paste('actions');
        if (actions !== null) {
            actions = actions.map(a => this.ActionService.create(a));
            actions.forEach(action => {
                this.addAction(action);
            });
            this.ToastService.info(actions.length + 'action[s] pasted from clipboard');
            this.hasChanged = true;
        }
    }

    openRecorder() {
        this.ActionRecorderService.open()
            .then(actions => this.addActions(actions));
    }

    /**
     * Toggles the disabled flag on an action.
     *
     * @param {Object} action - The action to enable or disable.
     */
    toggleDisableAction(action) {
        action.disabled = !action.disabled;
        this.hasChanged = true;
    }
}

export const symbolViewComponent = {
    controller: SymbolViewComponent,
    controllerAs: 'vm',
    template: require('./symbol-view.component.html')
};
