(function () {
    'use strict';

    angular
        .module('ALEX.modals')
        .controller('ActionEditModalController', ActionEditModalController);

    ActionEditModalController.$inject = [
        '$scope', '$modalInstance', 'modalData', 'ActionService', 'SymbolResource', 'SessionService'
    ];

    /**
     * The controller for the modal dialog that handles the editing of an action.
     *
     * The template can be found at 'views/modals/action-edit-modal.html'.
     *
     * @param $scope - The controllers scope
     * @param $modalInstance - The model instance
     * @param modalData - The data that is passed to this controller
     * @param ActionService - The Service for creating actions
     * @param SymbolResource - The API Resource for symbols
     * @param Session - The SessionService
     * @constructor
     */
    function ActionEditModalController($scope, $modalInstance, modalData, ActionService, SymbolResource, Session) {

        // the project in the session
        var project = Session.project.get();

        /**
         * The copy of the action that should be edited
         * @type {Object}
         */
        $scope.action = angular.copy(modalData.action);

        /**
         *
         * @type {Array}
         */
        $scope.symbols = [];

        (function init() {
            SymbolResource.getAll(project.id)
                .then(function (symbols) {
                    $scope.symbols = symbols;
                })
        }());

        /**
         * Close the modal dialog and pass the updated action to the handle that called it
         */
        $scope.updateAction = function () {

            // because actions are identified by temporary id
            // a new action has to be build and given the old id manually
            var id = $scope.action._id;
            $scope.action = ActionService.buildFromData($scope.action);
            $scope.action._id = id;
            $modalInstance.close($scope.action);
        };

        /**
         * Close the modal dialog without passing any data
         */
        $scope.closeModal = function () {
            $modalInstance.dismiss();
        }
    }
}());