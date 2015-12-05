import {events} from '../../constants';

/**
 * The controller for the modal window that handles editing a user.
 * This should only be called by an admin.
 *
 * @param $scope
 * @param $modalInstance
 * @param modalData
 * @param UserResource
 * @param ToastService
 * @param PromptService
 * @param EventBus
 * @constructor
 */
// @ngInject
class UserEditModalController {

    /**
     * Constructor
     * @param $modalInstance
     * @param modalData
     * @param UserResource
     * @param ToastService
     * @param PromptService
     * @param EventBus
     */
    constructor($modalInstance, modalData, UserResource, ToastService, PromptService, EventBus) {
        this.$modalInstance = $modalInstance;
        this.UserResource = UserResource;
        this.ToastService = ToastService;
        this.PromptService = PromptService;
        this.EventBus = EventBus;

        /**
         * The error message in case the update goes wrong
         * @type {null|string}
         */
        this.error = null;

        /**
         * The user to edit
         * @type {User}
         */
        this.user = modalData.user;
    }


    /** Updates the user on the server and closes the modal window on success */
    updateUser() {
        this.error = null;

        this.UserResource.update(this.user)
            .then(() => {
                this.EventBus.emit(events.USER_UPDATED, {user: this.user});
                this.ToastService.success('User updated successfully');
                this.$modalInstance.dismiss();
            })
            .catch(response => {
                this.error = response.data.message;
            });
    }

    /** Deletes a user */
    deleteUser() {
        this.error = null;

        this.PromptService.confirm('Do you want to delete this user permanently?')
            .then(() => {
                this.UserResource.remove(this.user)
                    .then(() => {
                        this.EventBus.emit(events.USER_DELETED, {user: this.user});
                        this.ToastService.success('The user has been deleted');
                        this.$modalInstance.dismiss();
                    })
                    .catch(response => {
                        this.error = response.data.message;
                    });
            });
    }

    /** Closes the modal window */
    close() {
        this.$modalInstance.dismiss();
    }
}

export default UserEditModalController;