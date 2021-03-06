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

import {LearnConfiguration} from '../../../entities/learner-configuration';

/**
 * The directive that handles the opening of the modal dialog for manipulating a learn configuration. Can only be
 * used as an attribute and attaches a click event to the source element that opens the modal.
 *
 * Attribute 'learnConfiguration' should be the model with a LearnConfiguration object instance.
 *
 * @param $uibModal - The ui.boostrap $modal service.
 * @returns {{restrict: string, scope: {learnConfiguration: string}, link: Function}}
 */
// @ngInject
export function learnerSetupSettingsModalHandleDirective($uibModal) {
    return {
        restrict: 'A',
        scope: {
            learnConfiguration: '=',
            onUpdate: '&'
        },
        link(scope, el) {
            el.on('click', () => {
                $uibModal.open({
                    component: 'learnerSetupSettingsModal',
                    resolve: {
                        modalData: () => ({
                            learnConfiguration: new LearnConfiguration(scope.learnConfiguration)
                        })
                    }
                }).result.then(config => scope.onUpdate({config}));
            });
        }
    };
}
