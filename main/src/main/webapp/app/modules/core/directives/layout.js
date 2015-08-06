(function () {
    'use strict';

    angular
        .module('ALEX.core')
        .controller('LayoutController', ['$scope', function ($scope) {
            $scope.collapsed = false;

            this.toggleCollapsed = function () {
                $scope.collapsed = !$scope.collapsed;
            };

            this.isCollapsed = function () {
                return $scope.collapsed;
            }
        }])

        .directive('layout', function () {
            return {
                scope: {},
                controller: 'LayoutController'
            }
        })

        .directive('layoutSidebar', function () {
            return {
                require: '^layout',
                link: function (scope, el, attrs, ctrl) {
                    scope.$watch(ctrl.isCollapsed, function (collapsed) {
                        if (collapsed) {
                            el.addClass('collapsed');
                        } else {
                            el.removeClass('collapsed');
                        }
                    });
                }
            }
        })

        .directive('layoutMain', function () {
            return {
                require: '^layout',
                link: function (scope, el, attrs, ctrl) {
                    scope.$watch(ctrl.isCollapsed, function (collapsed) {
                        el[0].style.left = collapsed ? '54px' : '270px';
                    });
                }
            }
        })

        .directive('layoutToggleButton', function () {
            return {
                require: '^layout',
                link: function (scope, el, attrs, ctrl) {
                    el.on('click', function () {
                        scope.$apply(function(){
                            ctrl.toggleCollapsed();
                        });
                    })
                }
            }
        })
}());