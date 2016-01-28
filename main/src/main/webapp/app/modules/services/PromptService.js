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

/** The controller of the prompt dialog */
// @ngInject
class PromptDialogController {

    /**
     * Constructor
     * @param $modalInstance
     * @param modalData
     */
    constructor($modalInstance, modalData) {
        this.$modalInstance = $modalInstance;

        /** The model for the input field for the user input **/
        this.userInput = null;

        /** The text to display **/
        this.text = modalData.text;

        /** The regex the user input has to match **/
        this.inputPattern = /^[a-zA-Z0-9\.\-,_]+$/;
    }

    /** Close the modal dialog and pass the user input */
    ok() {
        this.$modalInstance.close(this.userInput);
    }

    /** Close the modal dialog */
    close() {
        this.$modalInstance.dismiss();
    }
}


/** The controller that handles the confirm modal dialog. */
// @ngInject
class ConfirmDialogController {

    /**
     * Constructor
     * @param $modalInstance
     * @param modalData
     */
    constructor($modalInstance, modalData) {
        this.$modalInstance = $modalInstance;

        /**
         * The text to display
         * @type {string}
         */
        this.text = modalData.text || null;
    }

    /** Close the modal dialog */
    ok() {
        this.$modalInstance.close();
    }

    /** Close the modal dialog */
    close() {
        this.$modalInstance.dismiss();
    }
}


/** The service for handling promt and confirm dialogs */
// @ngInject
class PromptService {

    /**
     * Constructor
     * @param $modal
     */
    constructor($modal) {
        this.$modal = $modal;
    }

    /**
     * Opens the prompt dialog.
     *
     * @param {string} text - The text to display
     * @param {{regexp: string, errorMsg: string}} options}
     * @return {*} - The modal result promise
     */
    prompt(text, options) {
        return this.$modal.open({
            templateUrl: 'views/modals/prompt-dialog.html',
            controller: PromptDialogController,
            controllerAs: 'vm',
            resolve: {
                modalData: function () {
                    return {
                        text: text
                    };
                }
            }
        }).result;
    }

    /**
     * Opens the confirm dialog
     *
     * @param {string} text - The text to be displayed in the confirm dialog
     * @returns {*} - The modal result promise
     */
    confirm(text) {
        return this.$modal.open({
            templateUrl: 'views/modals/confirm-dialog.html',
            controller: ConfirmDialogController,
            controllerAs: 'vm',
            resolve: {
                modalData: function () {
                    return {text: text};
                }
            }
        }).result;
    }
}

export {PromptDialogController, ConfirmDialogController, PromptService};