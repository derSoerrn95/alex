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
import {Selectable} from '../../../utils/selectable';
import {SymbolGroupUtils} from '../../../utils/symbol-group-utils';

/**
 * The controller that handles the preparation of a learn process. Lists all symbol groups and its visible symbols.
 */
class LearnerSetupViewComponent {

    /**
     * Constructor.
     *
     * @param $state
     * @param {SymbolGroupResource} SymbolGroupResource
     * @param {SessionService} SessionService
     * @param {LearnerResource} LearnerResource
     * @param {ToastService} ToastService
     * @param {LearnResultResource} LearnResultResource
     * @param {SettingsResource} SettingsResource
     */
    // @ngInject
    constructor($state, SymbolGroupResource, SessionService, LearnerResource, ToastService, LearnResultResource,
                SettingsResource) {
        this.$state = $state;
        this.LearnerResource = LearnerResource;
        this.ToastService = ToastService;

        /**
         * The project that is in the session.
         * @type {Project}
         */
        this.project = SessionService.getProject();

        /**
         * All symbol groups that belong the the sessions project.
         * @type {SymbolGroup[]}
         */
        this.groups = [];

        /**
         * The learn results of previous learn processes.
         * @type {LearnResult[]}
         */
        this.learnResults = [];

        /**
         * A list of all symbols of all groups that is used in order to select them.
         * @type {AlphabetSymbol[]}
         */
        this.symbols = [];

        /**
         * A list of selected Symbols.
         * @type {Selectable}
         */
        this.selectedSymbols = new Selectable(this.symbols, 'id');

        /**
         * The configuration that is send to the server for learning.
         * @type {LearnConfiguration}
         */
        this.learnConfiguration = new LearnConfiguration();
        this.learnConfiguration.urls = [this.project.getDefaultUrl()];

        /**
         * The symbol that should be used as a reset symbol.
         * @type {AlphabetSymbol|null}
         */
        this.resetSymbol = null;

        /**
         * The latest learner result in the project.
         * @type {?LearnResult}
         */
        this.latestLearnerResult = null;

        SettingsResource.getSupportedWebDrivers()
            .then(data => {
                this.learnConfiguration.driverConfig.name = data.defaultWebDriver;
            })
            .catch(console.log);

        // make sure that there isn't any other learn process active
        // redirect to the load screen in case there is an active one
        this.LearnerResource.getStatus(this.project.id)
            .then(data => {
                if (data.active) {
                    if (data.project === this.project.id) {
                        this.ToastService.info('There is an active learning process for this project.');
                        this.$state.go('learnerStart', {projectId: this.project.id});
                    } else {
                        this.ToastService.info('There is an active learning process for another project.');
                    }
                } else {

                    // load all symbols in case there isn't any active learning process
                    SymbolGroupResource.getAll(this.project.id, true)
                        .then(groups => {
                            this.groups = groups;
                            this.symbols = SymbolGroupUtils.getSymbols(this.groups);
                            this.selectedSymbols = new Selectable(this.symbols, 'id');
                        })
                        .catch(console.error);

                    // load learn results so that their configuration can be reused
                    LearnResultResource.getAll(this.project.id)
                        .then(learnResults => this.learnResults = learnResults)
                        .catch(console.error);

                    LearnResultResource.getLatest(this.project.id)
                        .then(latestLearnerResult => this.latestLearnerResult = latestLearnerResult)
                        .catch(console.error);
                }
            })
            .catch(console.error);
    }

    /** @param {LearnConfiguration} config - The config to use. */
    setLearnConfiguration(config) {
        this.learnConfiguration = config;
    }

    /** @param {AlphabetSymbol} symbol - The symbol that will be used to reset the sul. */
    setResetSymbol(symbol) {
        this.resetSymbol = symbol;
    }

    /**
     * Starts the learning process if symbols are selected and a reset symbol is defined. Redirects to the
     * learning load screen on success.
     */
    startLearning() {
        if (this.resetSymbol === null) {
            this.ToastService.danger('You <strong>must</strong> selected a reset symbol in order to start learning!');
        } else {

            const selectedSymbols = this.selectedSymbols.getSelected();
            if (selectedSymbols.length > 0) {
                const i = selectedSymbols.findIndex(s => s.id === this.resetSymbol.id);
                if (i > -1) selectedSymbols.splice(i, 1);

                const config = JSON.parse(JSON.stringify(this.learnConfiguration));
                config.symbols = selectedSymbols.map(s => s.id);
                config.resetSymbol = this.resetSymbol.id;
                config.urls = this.learnConfiguration.urls.map(u => u.id);

                // start learning
                this.LearnerResource.start(this.project.id, config)
                    .then(() => {
                        this.ToastService.success('Learn process started successfully.');
                        this.$state.go('learnerStart', {projectId: this.project.id});
                    })
                    .catch(response => {
                        this.ToastService.danger('<p><strong>Start learning failed</strong></p>' + response.data.message);
                    });
            } else {
                this.ToastService.danger('You <strong>must</strong> at least select one symbol to start learning');
            }
        }
    }

    /**
     * Reuse the properties of a previous learn result for the current one.
     *
     * @param {LearnResult} result - The learn result from that the configuration should be reused.
     */
    reuseConfigurationFromResult(result) {
        this.learnConfiguration.algorithm = result.algorithm;
        this.learnConfiguration.eqOracle = result.steps[0].eqOracle;
        this.learnConfiguration.maxAmountOfStepsToLearn = result.maxAmountOfStepsToLearn;
        this.learnConfiguration.driverConfig = result.driverConfig;
        this.learnConfiguration.urls = result.urls;

        SymbolGroupUtils.getSymbols(this.groups).forEach(symbol => {
            if (result.symbols.indexOf(symbol.id) > -1) {
                this.selectedSymbols.select(symbol);
            }
            if (symbol.id === result.resetSymbol) {
                this.resetSymbol = symbol;
            }
        });
    }
}

export const learnerSetupViewComponent = {
    controller: LearnerSetupViewComponent,
    controllerAs: 'vm',
    template: require('./learner-setup-view.component.html')
};
