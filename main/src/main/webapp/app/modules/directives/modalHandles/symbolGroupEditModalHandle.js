/*
 * Copyright 2016 TU Dortmund
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

import {SymbolGroup} from '../../entities/SymbolGroup';

/**
 * The directive that handles the opening of the modal for editing or deleting a symbol group. Can only be used as
 * attribute and attaches a click event to the source element that opens the modal.
 *
 * Attribute 'group' - The model for the symbol group
 *
 * Use: '<button symbol-group-edit-modal group="..." on-updated="..." on-deleted="...">Click Me!</button>'
 *
 * @param $modal - The ui.bootstrap $modal service
 * @returns {{scope: {group: string}, link: link}}
 */
// @ngInject
function symbolGroupEditModalHandle($modal) {
    return {
        restrict: 'A',
        scope: {
            group: '='
        },
        link: link
    };

    function link(scope, el) {
        el.on('click', () => {
            $modal.open({
                templateUrl: 'views/modals/symbol-group-edit-modal.html',
                controller: 'SymbolGroupEditModalController',
                controllerAs: 'vm',
                resolve: {
                    modalData: function () {
                        return {group: new SymbolGroup(scope.group)};
                    }
                }
            });
        });
    }
}

export default symbolGroupEditModalHandle;