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

import {ActionService} from '../services/action.service';

const actionService = new ActionService();

/**
 * The symbol model.
 */
export class AlphabetSymbol {

    /**
     * Constructor.
     *
     * @param {object} obj - The object to create the symbol from.
     */
    constructor(obj = {}) {

        /**
         * The unique name of the symbol.
         * @type {string}
         */
        this.name = obj.name || null;

        /**
         * The id of the group the symbol should be created in.
         * @type {number}
         */
        this.group = obj.group || null;

        /**
         * The id of the symbol.
         * @type {number}
         */
        this.id = obj.id;

        /**
         * The id of the project the symbol belongs to.
         * @type {number}
         */
        this.project = obj.project;

        /**
         * The flag if the symbol has been deleted.
         * @type {boolean}
         */
        this.hidden = obj.hidden;

        /**
         * The custom output of the symbol on success.
         * @type {String}
         */
        this.successOutput = obj.successOutput;

        /**
         * The list of input variables.
         * @type {Object[]}
         */
        this.inputs = obj.inputs || [];

        /**
         * The list of output variables.
         * @type {Object[]}
         */
        this.outputs = obj.outputs || [];

        /**
         * The actions of the symbol.
         * @type {Action[]}
         */
        this.actions = obj.actions ? obj.actions.map(action => actionService.create(action)) : [];
    }

    /**
     * Gets the number of enabled actions.
     *
     * @returns {number}
     */
    countEnabledActions() {
        return this.actions.filter(action => !action.disabled).length;
    }

    /**
     * Gets a reduced version of the symbol that can be used to export it.
     *
     * @returns {{name: *, actions: Action[]}}
     */
    getExportableSymbol() {
        const symbol = JSON.parse(JSON.stringify(this));
        symbol.inputs.forEach(input => delete input.id);
        symbol.outputs.forEach(output => delete output.id);

        return {
            name: symbol.name,
            actions: symbol.actions,
            successOutput: symbol.successOutput,
            inputs: symbol.inputs,
            outputs: symbol.outputs
        };
    }
}
