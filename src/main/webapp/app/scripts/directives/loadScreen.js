(function () {
    'use strict';

    angular
        .module('weblearner.directives')
        .directive('loadScreen', loadScreen);

    loadScreen.$inject = ['$http', 'paths'];

    /**
     * The load screen that is shown during http requests. It lays over the application to prevent further
     * interactions with the page. The navigation is still usable. Add it right after the body and give the element
     * a high value for z-index in the stylesheet.
     *
     * Use is like '<load-screen></load-screen>'.
     *
     * @param $http - The angular $http service
     * @param {Object} paths - The constant with application paths
     * @returns {{scope: {}, templateUrl: string, link: link}}
     */
    function loadScreen($http, paths) {

        // the directive
        return {
            scope: {},
            templateUrl: paths.views.DIRECTIVES + '/load-screen.html',
            link: link
        };

        // the directives behaviour
        function link(scope, el, attrs) {

            /**
             * Shows if there are currently any active http requests going on
             *
             * @returns {boolean} - If there are any active requests
             */
            scope.hasPendingRequests = function () {
                return $http.pendingRequests.length > 0;
            };

            // watch the change of pendingRequests and change the visibility of the loadscreen
            scope.$watch(scope.hasPendingRequests, function (value) {
                if (value) {
                    el[0].style.display = 'block';
                } else {
                    el[0].style.display = 'none';
                }
            });
        }
    }
}());