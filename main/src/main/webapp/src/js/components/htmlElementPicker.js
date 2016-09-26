/*
 * Copyright 2016 TU Dortmund
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
 * The actual HTML element picker. Handles the complete window including the selection of elements and loading
 * of urls. Works as a 'mini embedded browser'
 *
 * Use: '<html-element-picker></html-element-picker>'
 *
 * @param SessionService - The SessionService
 */
class HtmlElementPickerComponent {

    /**
     * Constructor.
     * @param {SessionService} SessionService
     * @param {HtmlElementPickerService} HtmlElementPickerService
     * @param $element
     * @param $scope
     */
    // @ngInject
    constructor(SessionService, HtmlElementPickerService, $element, $scope) {
        this.SessionService = SessionService;
        this.HtmlElementPickerService = HtmlElementPickerService;
        this.iframe = $element.find('iframe');
        this.$scope = $scope;

        this.proxyUrl = null;

        // when moving with the mouse over an element, this elements gets saved in this variable in order to
        // prevent multiple calls of getCssPath for the same element
        this.lastTarget = null;

        /**
         * flag for selection mode
         * @type {boolean}
         */
        this.isSelectable = false;

        /**
         * The XPath of the selected element
         * @type {null|string}
         */
        this.selector = null;

        /**
         * The element.textContent value
         * @type {null|string}
         */
        this.textContent = null;

        /**
         * The url that is loaded in the iframe
         * @type {string}
         */
        this.url = null;

        /**
         * The project in the session
         * @type {null|Project}
         */
        this.project = null;

        this.mouseMoveHandler = null;
        this.keyUpHandler = null;
        this.clickHandler = null;

        this.init();
    }

    /**
     * Load project, create proxy address and load the last url in the iframe.
     */
    init() {
        this.project = this.SessionService.getProject();
        this.proxyUrl = 'rest/proxy?url=';
        this.url = this.HtmlElementPickerService.lastUrl;
        this.loadUrl();
    }

    /**
     * Loads an entered url into the iframe and handles the click on every a element
     */
    loadUrl() {
        const self = this;

        this.iframe.attr('src', this.proxyUrl + this.project.baseUrl + '/' + (this.url === null ? '' : this.url));
        this.iframe.on('load', () => {
            angular.element(this.iframe.contents()[0].body.getElementsByTagName('a'))
                .on('click', function () {
                    if (!self.isSelectable) {
                        const _this = this;
                        if (this.getAttribute('href') !== '' && this.getAttribute('href')[0] !== '#') {
                            self.$scope.$apply(() => {
                                self.url = decodeURIComponent(_this.getAttribute('href'))
                                    .replace(window.location.origin + '/' + self.proxyUrl + self.project.baseUrl, '', '');
                            });
                        }
                    }
                });
        });
    }

    /**
     * Get the unique CSS XPath from selected Element
     * http://stackoverflow.com/questions/4588119/get-elements-css-selector-without-element-id
     *
     * @param el  - The element to get the unique css path from
     * @returns {String} - The unique css path ot the element
     */
    getCssPath(el) {
        var names = [];
        while (el.parentNode) {
            if (el.id) {
                names.unshift('#' + el.id);
                break;
            } else {
                if (el == el.ownerDocument.documentElement) names.unshift(el.tagName);
                else {
                    for (var c = 1, e = el; e.previousElementSibling; e = e.previousElementSibling, c++);
                    names.unshift(el.tagName + ":nth-child(" + c + ")");
                }
                el = el.parentNode;
            }
        }
        return names.join(" > ");
    }

    /**
     * Makes the web element picker invisible and fires the close event
     */
    close() {
        this.HtmlElementPickerService.lastUrl = this.url;
        this.HtmlElementPickerService.deferred.reject();
    }

    /**
     * Makes the web element Picker invisible and fires the ok event with the selector of the element that was
     * selected. If no selector is defined, then it just closes the picker
     */
    ok() {
        this.HtmlElementPickerService.lastUrl = this.url;
        this.HtmlElementPickerService.deferred.resolve({
            selector: this.selector,
            textContent: this.textContent
        });
    }

    /**
     * Removes the outline from the selected element, removes all events from the iframe and removes the
     * keypress event. When this function is called the selected element is fixed and won't change by any
     * further interaction with the iframe
     *
     * @param e - js event
     */
    handleClick(e) {
        if (typeof e !== "undefined") {
            e.preventDefault();
            e.stopPropagation();
        }

        if (this.lastTarget !== null) {
            this.lastTarget.style.outline = '0px';
        }
        this.lastTarget = null;

        angular.element(this.iframe.contents()[0].body).off('mousemove', this.mouseMoveHandler);
        angular.element(this.iframe.contents()[0].body).off('click', this.clickHandler);
        document.body.removeEventListener('keyup', this.keyUpHandler);
    }

    /**
     * Saves the element that is under the cursor so that it can be selected. Adds an outline to the element
     * in order to highlight it.
     *
     * @param e - js event
     * @returns {boolean}
     */
    handleMouseMove(e) {
        if (this.lastTarget === e.target) {
            return false;
        } else {
            if (this.lastTarget !== null) {
                this.lastTarget.style.outline = '0px';
            }
            this.lastTarget = e.target;
        }
        this.lastTarget.style.outline = '5px solid red';
        this.selector = this.getCssPath(this.lastTarget);

        if (this.lastTarget.nodeName.toLowerCase() === 'input') {
            this.textContent = this.lastTarget.value;
        } else {
            this.textContent = this.lastTarget.textContent;
        }

        this.$scope.$apply();
    }

    /**
     * Calls handleClick() when control key is pressed to have an alternative for selecting a dom node without
     * firing any click events on it.
     *
     * @param e
     */
    handleKeyUp(e) {
        if (e.keyCode == 17) { // strg
            this.handleClick();
            this.isSelectable = false;
        }
    }

    /**
     * Enables the selection mode and therefore adds events to the iframe
     */
    toggleSelection() {
        if (!this.isSelectable) {
            const iframeBody = angular.element(this.iframe.contents()[0].body);
            this.mouseMoveHandler = this.handleMouseMove.bind(this);
            this.keyUpHandler = this.handleKeyUp.bind(this);

            this.clickHandler = (e) => {
                this.handleClick(e);
                this.$scope.$apply(() => {
                    this.isSelectable = false;
                });
            };

            iframeBody.on('mousemove', this.mouseMoveHandler);
            iframeBody.one('click', this.clickHandler);
            document.body.addEventListener('keyup', this.keyUpHandler, false);
        } else {
            this.handleClick();
            this.selector = null;
        }
        this.isSelectable = !this.isSelectable;
    }
}

export const htmlElementPicker = {
    templateUrl: 'html/components/html-element-picker.html',
    controller: HtmlElementPickerComponent,
    controllerAs: 'vm'
};