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

import {actionType} from '../../../constants';
import {Action} from '../action';

/**
 * Action to validate a JSON object against a JSON schema.
 */
export class ValidateJsonAction extends Action {

    /**
     * Constructor.
     * @param obj - The action.
     */
    constructor(obj) {
        super(actionType.REST_VALIDATE_JSON, obj);

        /**
         * The JSON schema to validate the body of the latest request against.
         * @type {string}
         */
        this.schema = obj.schema || '{}';
    }

    toString() {
        return `Validate the JSON object of the latest request.`;
    }
}
