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

import {events} from '../../../constants';
import {AlphabetSymbol} from '../../../entities/alphabet-symbol';

/**
 * Handles the behaviour of the modal to edit an existing symbol and updates the edited symbol on the server.
 */
export class SymbolEditModalComponent {

    /**
     * Constructor.
     *
     * @param {SymbolResource} SymbolResource
     * @param {ToastService} ToastService
     * @param {EventBus} EventBus
     */
    // @ngInject
    constructor(SymbolResource, ToastService, EventBus) {
        this.SymbolResource = SymbolResource;
        this.ToastService = ToastService;
        this.EventBus = EventBus;

        /**
         * The symbol to edit.
         * @type {AlphabetSymbol}
         */
        this.symbol = null;

        /**
         * A copy of the old symbol.
         * @type {AlphabetSymbol}
         */
        this.symbolCopy = null;

        /**
         * The error message that is displayed when update fails.
         * @type {null|string}
         */
        this.errorMsg = null;
    }

    $onInit() {
        this.symbol = this.resolve.modalData.symbol;
        this.symbolCopy = new AlphabetSymbol(this.symbol);
    }

    /**
     * Make a request to the API in order to update the symbol. Close the modal on success.
     */
    updateSymbol() {
        this.errorMsg = null;

        // do not update on server
        const updateOnServer = this.resolve.modalData.updateOnServer;
        if ((updateOnServer !== undefined) && !updateOnServer) {
            this.EventBus.emit(events.SYMBOL_UPDATED, {
                newSymbol: this.symbol,
                oldSymbol: this.symbolCopy
            });
            this.dismiss();
            return;
        }

        // update the symbol and close the modal dialog on success with the updated symbol
        this.SymbolResource.update(this.symbol)
            .then(updatedSymbol => {
                this.ToastService.success('Symbol updated');
                this.EventBus.emit(events.SYMBOL_UPDATED, {symbol: updatedSymbol});
                this.dismiss();
            })
            .catch(response => {
                this.errorMsg = response.data.message;
            });
    }
}

export const symbolEditModalComponent = {
    template: require('./symbol-edit-modal.component.html'),
    bindings: {
        dismiss: '&',
        resolve: '='
    },
    controller: SymbolEditModalComponent,
    controllerAs: 'vm',
};
