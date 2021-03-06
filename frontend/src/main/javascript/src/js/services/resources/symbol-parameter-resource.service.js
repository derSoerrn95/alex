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

import {apiUrl} from '../../../../environments';

/** The resource for symbol parameters. */
export class SymbolParameterResource {

    /**
     * Constructor.
     *
     * @param $http
     */
    // @ngInject
    constructor($http) {
        this.$http = $http;
    }

    _uri(projectId, symbolId) {
        return `${apiUrl}/projects/${projectId}/symbols/${symbolId}/parameters`;
    }

    /**
     * Create a symbol parameter.
     *
     * @param {number} projectId The id of the project.
     * @param {number} symbolId The id of the symbol.
     * @param {object} parameter The parameter to create.
     */
    create(projectId, symbolId, parameter) {
        return this.$http.post(this._uri(projectId, symbolId), parameter)
            .then(res => res.data);
    }

    /**
     * update a symbol parameter.
     *
     * @param {number} projectId The id of the project.
     * @param {number} symbolId The id of the symbol.
     * @param {object} parameter The parameter to create.
     */
    update(projectId, symbolId, parameter) {
        return this.$http.put(`${this._uri(projectId, symbolId)}/${parameter.id}`, parameter)
            .then(res => res.data);
    }

    /**
     * Delete a symbol parameter.
     *
     * @param {number} projectId The id of the project.
     * @param {number} symbolId The id of the symbol.
     * @param {number} parameterId The id of  the parameter.
     */
    remove(projectId, symbolId, parameterId) {
        return this.$http.delete(`${this._uri(projectId, symbolId)}/${parameterId}`);
    }
}
