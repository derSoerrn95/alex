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
 * This directive changes the dimensions of an element to its parent element. Optionally you can trigger this
 * behaviour by passing the value 'true' to the parameter bindResize so that every time the window resizes,
 * the dimensions of the element will be updated.
 */
class ResponsiveIframeComponent {

    /**
     * Constructor.
     *
     * @param $window
     * @param $element
     * @param $scope
     */
    // @ngInject
    constructor($window, $element, $scope) {

        /**
         * The iframe node element.
         */
        this.iframe = $element.find('iframe')[0];

        /**
         * The container of the iframe which is used to fit the iframes size to.
         */
        this.container = $element[0].parentNode;

        /**
         * The resize handler.
         */
        this.resizeHandler = this.fitToParent.bind(this);

        $window.addEventListener('resize', this.resizeHandler, false);

        $scope.$on('$destroy', () => {
            $window.removeEventListener('resize', this.resizeHandler, false);
        });

        this.fitToParent();
    }

    $onInit() {
        window.setTimeout(() => {
            window.dispatchEvent(new Event('resize'));
        }, 100);
    }

    /**
     * Adjusts the size of the iframe to the size of the container element.
     */
    fitToParent() {
        this.iframe.setAttribute('height', this.container.offsetHeight);
        this.iframe.setAttribute('width', this.container.offsetWidth);
    }
}

export const responsiveIframeComponent = {
    template: require('./responsive-iframe.component.html'),
    controller: ResponsiveIframeComponent,
    controllerAs: 'vm'
};
