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

/**
 * The directive for handling the opening of the modal for creating a new symbol group. Can only be used as
 * an attribute and attaches a click event to its source element.
 *
 * Use: '<button symbol-group-create-modal-handle>Click Me!</button>'
 *
 * @param $modal - The ui.bootstrap $modal service
 * @returns {{restrict: string, link: link}}
 */
// @ngInject
function symbolGroupCreateModalHandle($modal) {
    return {
        restrict: 'A',
        link: link
    };

    function link(scope, el) {
        el.on('click', () => {
            $modal.open({
                templateUrl: 'views/modals/symbol-group-create-modal.html',
                controller: 'SymbolGroupCreateModalController',
                controllerAs: 'vm'
            });
        });
    }
}

export default symbolGroupCreateModalHandle;