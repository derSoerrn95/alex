(function () {
    "use strict";

    angular
        .module('ALEX.controllers')
        .controller('UserSettingsController', UserSettingsController);

    /**
     * The main controller for the page users settings page
     *
     * @param $scope
     * @param UserResource
     * @param SessionService
     * @constructor
     */
    // @ngInject
    function UserSettingsController($scope, UserResource, SessionService) {

        // the user from the jwt
        var user = SessionService.user.get();

        /**
         * The user to edit
         * @type {null}
         */
        $scope.user = null;

        // fetch the user from the api
        UserResource.get(user.id)
            .then(function (user) {
                $scope.user = user;
            });
    }
}());