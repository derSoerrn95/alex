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

import {PROPERTY_NAME} from '../checkbox/checkbox.component';

/**
 * The controller of the checkbox for selecting multiple items.
 *
 * Use: <checkbox-multiple model="..." model-fn="..."></checkbox-multiple> where
 * 'model' should be a list of objects
 * 'modeFn' should be a function that returns a list of objects.
 * Only one attribute should be given at a time.
 */
class CheckboxMultipleComponent {

    /**
     * Constructor.
     */
    constructor() {

        /**
         * The status of the items.
         * @type {boolean}
         */
        this.checked = false;
    }

    /**
     * Selects or deselects all items.
     */
    toggleSelectItems() {
        this.checked = !this.checked;

        if (this.model) {
            this.model.forEach(item => item[PROPERTY_NAME] = this.checked);
        } else if (this.modelFn) {
            const items = this.modelFn();
            items.forEach(item => item[PROPERTY_NAME] = this.checked);
        }
    }
}

export const checkboxMultipleComponent = {
    bindings: {
        model: '=',
        modelFn: '&'
    },
    controller: CheckboxMultipleComponent,
    controllerAs: 'vm',
    template: require('./checkbox-multiple.component.html')
};
