(function () {
    'use strict';

    angular
        .module('weblearner.directives')
        .directive('fitParentDimensions', [
            '$window',
            fitParentDimensions
        ]);

    /**
     * fitParentDimensions
     *
     * This directive changes the dimensions of an element to its parent element. Optionally you can trigger this
     * behaviour by passing the value 'true' to the parameter bindResize so that every time the window resizes,
     * the dimensions of the element will be updated.
     *
     * By setting 'asStyle' to 'true', the dimensions will be written in the style attribute of the element. Otherwise
     * this directive creates the width and height attribute.
     *
     * As Default, both options are disabled.
     *
     * @param $window
     * @return {{scope: {bindResize: string}, link: link}}
     */
    function fitParentDimensions($window) {

        // the directive
        var directive = {
            restrict: 'A',
            scope: {
                bindResize: '=',
                asStyle: '='
            },
            link: link
        };
        return directive;

        //////////

        /**
         * @param scope
         * @param el
         * @param attrs
         */
        function link(scope, el, attrs) {

            var _parent = el.parent()[0];

            //////////

            if (scope.bindResize) {
                angular.element($window).on('resize', fitToParent)
            }

            fitToParent();

            //////////

            /**
             * Set the element to the dimensions of its parent
             */
            function fitToParent() {

                var width = _parent.offsetWidth;
                var height = _parent.offsetHeight;

                if (scope.asStyle) {
                    el[0].style.width = width + 'px';
                    el[0].style.height = height + 'px';
                } else {
                    el[0].setAttribute('width', width);
                    el[0].setAttribute('height', height);
                }
            }
        }
    }
}());