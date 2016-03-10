import Action from '../../../../../src/js/entities/actions/Action';
import SelectWebAction from '../../../../../src/js/entities/actions/webActions/SelectWebAction';
import {actionType} from '../../../../../src/js/constants';

describe('SelectWebAction', () => {
    beforeEach(angular.mock.module('ALEX'));

    it('should extend the default action and should implement a toString method', () => {
        const action = new SelectWebAction({});
        expect(action instanceof Action).toBe(true);
        expect(angular.isFunction(action.toString)).toBe(true);
    });

    it('should have create a default action', () => {
        const expectedAction = {
            type: actionType.WEB_SELECT,
            negated: false,
            ignoreFailure: false,
            disabled: false,

            node: '',
            value: '',
            selectBy: 'TEXT'
        };
        const action = new SelectWebAction({});
        expect(angular.toJson(action)).toEqual(angular.toJson(expectedAction));
    });
});
