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

/**
 * The directive that is used to handle the modal dialog for creating an action. Must be used as an attribute for
 * the attached element. It attaches a click event to the element that opens the modal dialog. Does NOT saves the
 * action on the server.
 *
 * Can be used like this: '<button action-create-modal-handle>Click Me!</button>'.
 *
 * @param $uibModal - The modal service.
 * @returns {{restrict: string, scope: {}, link: Function}}
 */
// @ngInject
export function actionCreateModalHandleDirective($uibModal) {
    return {
        restrict: 'A',
        scope: {
            onCreated: '&'
        },
        link(scope, el) {
            el.on('click', () => {
                $uibModal.open({
                    component: 'actionCreateModal',
                    size: 'lg',
                    resolve: {
                        modalData: () => ({
                            onCreated: scope.onCreated
                        })
                    }
                });
            });
        }
    };
}
