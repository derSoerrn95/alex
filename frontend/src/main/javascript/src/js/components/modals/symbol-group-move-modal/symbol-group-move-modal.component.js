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

import {events} from '../../../constants';
import {SymbolGroupUtils} from '../../../utils/symbol-group-utils';

export const symbolGroupMoveModalComponent = {
    template: require('./symbol-group-move-modal.component.html'),
    bindings: {
        resolve: '=',
        dismiss: '&',
        close: '&',
    },
    controllerAs: 'vm',
    controller: class SymbolGroupMoveModalComponent {

        /**
         * Constructor.
         *
         * @param {SymbolGroupResource} SymbolGroupResource
         * @param {ToastService} ToastService
         * @param {EventBus} EventBus
         */
        // @ngInject
        constructor(SymbolGroupResource, ToastService, EventBus) {
            this.symbolGroupResource = SymbolGroupResource;
            this.toastService = ToastService;
            this.eventBus = EventBus;

            /**
             * The error message to display.
             * @type {String}
             */
            this.errorMessage = null;

            /**
             * The group to move.
             * @type {SymbolGroup}
             */
            this.group = null;

            /**
             * List of all groups in the project.
             * @type {SymbolGroup[]}
             */
            this.groups = [];

            /**
             * The new parent group.
             * @type {SymbolGroup}
             */
            this.selectedGroup = null;
        }

        $onInit() {
            this.group = this.resolve.group;

            this.symbolGroupResource.getAll(this.group.project)
                .then(groups => {
                    this.groups = groups;

                    if (this.group.parent != null) {
                        this.selectedGroup = SymbolGroupUtils.findGroupById(this.groups, this.group.parent);
                    }
                });
        }

        /**
         * Moves the group.
         */
        moveGroup() {
            const fromGroupId = this.group.parent;
            this.group.parent = this.selectedGroup == null ? null : this.selectedGroup.id;

            this.symbolGroupResource.move(this.group)
                .then(movedGroup => {
                    this.toastService.success('The group has been moved');
                    this.eventBus.emit(events.GROUP_MOVED, {from: fromGroupId, group: movedGroup});
                    this.close({$value: movedGroup});
                })
                .catch(err => {
                    this.errorMessage = `The group could not be moved. ${err.data.message}`;
                    this.group.parent = fromGroupId;
                });
        }

        /**
         * Select the target group.
         * @param {SymbolGroup} group The selected group.
         */
        setSelectedGroup(group) {
            if (this.selectedGroup != null && this.selectedGroup.id === group.id) {
                this.selectedGroup = null;
            } else {
                this.selectedGroup = group;
            }
        }
    }
};
