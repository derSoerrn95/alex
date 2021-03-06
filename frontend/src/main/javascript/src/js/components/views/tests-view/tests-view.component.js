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
 * The view for the tests.
 * @type {{controller: testsView.controller, controllerAs: string, templateUrl: string}}
 */
export const testsViewComponent = {

    /**
     * The controller of the view.
     */
    controller: class {

        /**
         * Constructor.
         *
         * @param $state
         * @param {SessionService} SessionService
         * @param {TestResource} TestResource
         */
        // @ngInject
        constructor($state, SessionService, TestResource) {
            const project = SessionService.getProject();

            /**
             * The test case or test suite.
             * @type {object|object[]}
             */
            this.test = null;

            const testId = $state.params.testId;
            if (testId === 0) {
                TestResource.getRoot(project.id)
                    .then(data => this.test = data)
                    .catch(err => $state.go('error', {message: err.data.message}));
            } else {
                TestResource.get(project.id, testId)
                    .then(data => this.test = data)
                    .catch(err => $state.go('error', {message: err.data.message}));
            }
        }
    },
    controllerAs: 'vm',
    template: require('./tests-view.component.html')
};
