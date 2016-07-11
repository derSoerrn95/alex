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
 * The controller for the user register form component.
 */
class UserRegisterFrom {

    /**
     * Constructor.
     *
     * @param {UserResource} UserResource
     * @param {ToastService} ToastService
     */
    // @ngInject
    constructor(UserResource, ToastService) {
        this.UserResource = UserResource;
        this.ToastService = ToastService;


        /**
         * The email of the user.
         * @type {string}
         */
        this.email = null;

        /**
         * The password of the user.
         * @type {string}
         */
        this.password = null;
    }

    /**
     * Creates a new user.
     */
    register() {
        if (this.email && this.password) {
            this.UserResource.create(this.email, this.password)
                .then(() => {
                    this.ToastService.success('Registration successful');
                    this.email = null;
                    this.password = null;
                })
                .catch(response => {
                    this.ToastService.danger(`Registration failed. ${response.data.message}`);
                });
        } else {
            this.ToastService.info('Make sure your inputs are valid.');
        }
    }
}

export const userRegisterForm = {
    controller: UserRegisterFrom,
    controllerAs: 'vm',
    template: `
        <form name="vm.form" ng-submit="vm.register()" id="user-register-form">
            <div class="form-group">
                <label>Email</label>
                <input type="email" class="form-control" name="mail" placeholder="Email address" autofocus required ng-model="vm.email">

                <div class="help-block" ng-messages="vm.form.mail.$error" ng-if="vm.form.mail.$touched">
                    <div class="alert alert-danger alert-condensed" ng-message="required">
                        The email is required
                    </div>
                    <div class="alert alert-danger alert-condensed" ng-message="email">
                        You have to enter a valid email address
                    </div>
                </div>
            </div>
            <div class="form-group">
                <label>Password</label>
                <input type="password" class="form-control" name="password" placeholder="Password" required ng-minlength="1" ng-model="vm.password">

                <div class="help-block" ng-messages="vm.form.password.$error" ng-if="vm.form.password.$touched">
                    <div class="alert alert-danger alert-condensed" ng-message="required">
                        The password is required
                    </div>
                </div>
            </div>
            <button class="btn btn-sm btn-block btn-primary">Create Account</button>
        </form>
    `
};