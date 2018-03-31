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
 * The directive of the dashboard widget that displays the current status of the learner.
 */
class LearnerStatusWidgetComponent {

    /**
     * Constructor.
     *
     * @param {LearnerResource} LearnerResource
     * @param {ToastService} ToastService
     */
    // @ngInject
    constructor(LearnerResource, ToastService) {
        this.LearnerResource = LearnerResource;
        this.ToastService = ToastService;

        /**
         * Whether the learner is actively learning an application.
         * @type {boolean}
         */
        this.isActive = false;

        /**
         * Whether the learner has finished learning an application.
         * @type {boolean}
         */
        this.hasFinished = false;

        /**
         * The intermediate or final learning result.
         * @type {LearnResult}
         */
        this.result = null;

        /**
         * The current project.
         * @type {Project}
         */
        this.project = null;
    }

    $onInit() {
        this.LearnerResource.isActive(this.project.id)
            .then(data => {
                this.isActive = data.active;
                if (!data.active) {
                    this.LearnerResource.getStatus(this.project.id)
                        .then(data => {
                            if (data !== null) {
                                this.hasFinished = true;
                                this.result = data;
                            }
                        });
                }
            })
            .catch(err => console.log(err));
    }

    /**
     * Induces the Learner to stop learning after the current hypothesis model.
     */
    abort() {
        this.LearnerResource.stop(this.project.id)
            .then(() => {
                this.ToastService.info('The Learner stops with the next hypothesis');
            })
            .catch(err => console.log(err));
    }
}

export const learnerStatusWidgetComponent = {
    template: require('./learner-status-widget.component.html'),
    bindings: {
        project: '='
    },
    controller: LearnerStatusWidgetComponent,
    controllerAs: 'vm'
};
