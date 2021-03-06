import {Action} from '../../../../../src/js/entities/actions/action';
import {ClearWebAction} from '../../../../../src/js/entities/actions/web/clear-action';
import {actionType} from '../../../../../src/js/constants';

describe('ClearWebAction', () => {
    beforeEach(angular.mock.module('ALEX'));

    it('should extend the default action and should implement a toString method', () => {
        const action = new ClearWebAction({});
        expect(action instanceof Action).toBe(true);
        expect(angular.isFunction(action.toString)).toBe(true);
    });

    it('should create a default action', () => {
        const expectedAction = {
            type: actionType.WEB_CLEAR,
            negated: false,
            ignoreFailure: false,
            disabled: false,
            node: {
                selector: '',
                type: 'CSS'
            }
        };
        const action = new ClearWebAction({});
        expect(angular.toJson(action)).toEqual(angular.toJson(expectedAction));
    });
});
