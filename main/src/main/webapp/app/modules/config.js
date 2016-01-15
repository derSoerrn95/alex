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
 * Configure some third party libraries and set the http interceptor to send the jwt with each request
 * @param ngToastProvider
 * @param selectionModelOptionsProvider
 * @param jwtInterceptorProvider
 * @param $httpProvider
 */
// @ngInject
function config(ngToastProvider, selectionModelOptionsProvider, jwtInterceptorProvider, $httpProvider) {

    // configure ngToast toast position
    ngToastProvider.configure({
        verticalPosition: 'bottom',
        horizontalPosition: 'center',
        maxNumber: 1,
        additionalClasses: 'animate-toast'
    });

    // default options for selection model
    selectionModelOptionsProvider.set({
        selectedAttribute: '_selected',
        selectedClass: 'selected',
        mode: 'multiple',
        cleanupStrategy: 'deselect'
    });

    // pass the jwt with each request to the server
    jwtInterceptorProvider.tokenGetter = ['$window', $window => {
        return $window.sessionStorage.getItem('jwt');
    }];
    $httpProvider.interceptors.push('jwtInterceptor');

    // request the permission to send notifications only once
    if (("Notification" in window) && Notification.permission !== 'denied') {
        Notification.requestPermission(permission => {
            if (permission === "default") {
                const notification = new Notification("The cake is a lie!");
                setTimeout(notification.close.bind(notification), 3000);
            }
        });
    }
}

export const configuration = {
    config: config
};