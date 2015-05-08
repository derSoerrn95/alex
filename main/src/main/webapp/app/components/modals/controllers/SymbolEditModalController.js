(function () {
    'use strict';

    angular
        .module('ALEX.modals')
        .controller('SymbolEditModalController', SymbolEditModalController);

    SymbolEditModalController.$inject = [
        '$scope', '$modalInstance', 'modalData', 'Symbol', 'ToastService'
    ];

    /**
     * Handles the behaviour of the modal to edit an existing symbol and updates the edited symbol on the server.
     * The corresponding template for this modal can found under 'app/partials/modals/symbol-edit-modal.html'.
     *
     * @param $scope
     * @param $modalInstance
     * @param modalData
     * @param Symbol
     * @param Toast
     * @constructor
     */
    function SymbolEditModalController($scope, $modalInstance, modalData, Symbol, Toast) {

        /** The symbol that is passed to the modal. @type {Symbol} */
        $scope.symbol = modalData.symbol;

        /**
         * The error message that is displayed when update fails
         * @type {null|string}
         */
        $scope.errorMsg = null;

        // The copy of the symbol that will be passed back together with the updated one
        var copy = $scope.symbol.copy();

        /**
         * Make a request to the API in order to update the symbol. Close the modal on success.
         */
        $scope.updateSymbol = function () {
            $scope.errorMsg = null;

            // remove the selection from the symbol in case there is any
            for (var i = 0; i < $scope.symbols.length; i++) {
                delete $scope.symbols[i]._selected;
            }

            // do not update on server
            if (angular.isDefined(modalData.updateOnServer) && !modalData.updateOnServer) {
                $modalInstance.close({
                    new: $scope.symbol,
                    old: copy
                });
                return;
            }

            // update the symbol and close the modal dialog on success with the updated symbol
            Symbol.Resource.update($scope.symbol.project, $scope.symbol)
                .then(function (updatedSymbol) {
                    Toast.success('Symbol updated');
                    $modalInstance.close({
                        new: updatedSymbol,
                        old: copy
                    });
                })
                .catch(function (response) {
                    $scope.errorMsg = response.data.message;
                })
        };

        /**
         * Close the modal dialog
         */
        $scope.closeModal = function () {
            $modalInstance.dismiss();
        }
    }
}());