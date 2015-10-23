(function () {
    'use strict';

    angular
        .module('ALEX.core')
        .factory('LearnResult', LearnResultFactory);

    LearnResultFactory.$inject = ['LearnConfiguration', '_'];

    /**
     * The factory for the model of a learn result
     *
     * @param LearnConfiguration - The factory for LearnConfiguration
     * @param _ - Lodash
     * @returns {LearnResult}
     * @constructor
     */
    function LearnResultFactory(LearnConfiguration, _) {

        /**
         * The model of a learn result
         *
         * @constructor
         */
        function LearnResult() {
        }

        /**
         * Indicates if an error has been recorded during the step
         *
         * @returns {boolean}
         */
        LearnResult.prototype.hasError = function () {
            return angular.isDefined(this.error);
        };

        /**
         * Creates a new instance of a LearnResult from an object
         *
         * @param {Object} data - The object the learn result should be build from
         * @returns {LearnResult} - The instance of LearnResult from the data
         */
        LearnResult.build = function (data) {
            var learnResult = new LearnResult();
            angular.extend(learnResult, {
                configuration: LearnConfiguration.build(data.configuration),
                hypothesis: data.hypothesis,
                project: data.project,
                sigma: data.sigma,
                stepNo: data.stepNo,
                testNo: data.testNo,
                algorithmInformation: data.algorithmInformation,
                statistics: data.statistics,
                error: data.error,
                errorText: data.errorText
            });

            // convert ns to ms
            learnResult.statistics.startTime = Math.ceil(learnResult.statistics.startTime / 1000000);
            learnResult.statistics.duration = Math.ceil(learnResult.statistics.duration / 1000000);

            return learnResult;
        };

        /**
         * Creates LearnResult[s] from an API response
         *
         * @param response
         * @returns {*}
         */
        LearnResult.transformApiResponse = function (response) {
            if (angular.isArray(response.data)) {
                if (angular.isArray(response.data[0])) {
                    return _.forEach(response.data, function (completeResult) {
                        _.map(completeResult, LearnResult.build);
                    })
                } else {
                    return _.map(response.data, LearnResult.build);
                }
            } else {
                return LearnResult.build(response.data);
            }
        };

        return LearnResult;
    }
}());