(function () {
    'use strict';

    angular
        .module('ALEX.controllers')
        .controller('CountersController', CountersController);

    /**
     * The controller for the page that lists all counters of a project in a list. It is also possible to delete them.
     *
     * Template: 'views/counters.html';
     *
     * @param $scope - The projects scope
     * @param SessionService - The SessionService
     * @param CounterResource - The CounterResource
     * @param ToastService - The ToastService
     * @param _ - Lodash
     * @constructor
     */
    // @ngInject
    function CountersController($scope, SessionService, CounterResource, ToastService, _) {

        // the sessions project
        var project = SessionService.project.get();

        /**
         * The counters of the project
         * @type {{name: string, value: number, project: number}[]}
         */
        $scope.counters = [];

        /**
         * The selected counters objects
         * @type {{name: string, value: number, project: number}[]}
         */
        $scope.selectedCounters = [];

        // load all existing counters from the server
        (function init() {
            CounterResource.getAll(project.id)
                .then(function (counters) {
                    $scope.counters = counters;
                });
        }());

        /**
         * Delete a counter from the server and on success from scope
         *
         * @param {{name: string, value: number, project: number}} counter - The counter that should be deleted
         */
        $scope.deleteCounter = function (counter) {
            CounterResource.delete(project.id, counter.name)
                .then(function () {
                    ToastService.success('Counter "' + counter.name + '" deleted');
                    _.remove($scope.counters, {name: counter.name});
                })
                .catch(function (response) {
                    ToastService.danger('<p><strong>Deleting counter "' + counter.name + '" failed</strong></p>' + response.data.message);
                })
        };

        /**
         * Delete all selected counters from the server and on success from scope
         */
        $scope.deleteSelectedCounters = function () {
            if ($scope.selectedCounters.length > 0) {
                CounterResource.deleteSome(project.id, _.pluck($scope.selectedCounters, 'name'))
                    .then(function () {
                        ToastService.success('Counters deleted');
                        _.forEach($scope.selectedCounters, function (counter) {
                            _.remove($scope.counters, {name: counter.name});
                        })
                    })
                    .catch(function (response) {
                        ToastService.danger('<p><strong>Deleting counters failed</strong></p>' + response.data.message);
                    })
            }
        }
    }
}());