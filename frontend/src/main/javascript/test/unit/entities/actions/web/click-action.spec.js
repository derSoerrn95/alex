import {Action} from '../../../../../src/js/entities/actions/action';
import {ClickWebAction} from '../../../../../src/js/entities/actions/web/click-action';
import {actionType} from '../../../../../src/js/constants';

describe('ClickWebAction', () => {
    beforeEach(angular.mock.module('ALEX'));

    it('should extend the default action and should implement a toString method', () => {
        const action = new ClickWebAction({});
        expect(action instanceof Action).toBe(true);
        expect(angular.isFunction(action.toString)).toBe(true);
    });

    it('should create a default action', () => {
        const expectedAction = {
            type: actionType.WEB_CLICK,
            negated: false,
            ignoreFailure: false,
            disabled: false,
            node: {
                selector: '',
                type: 'CSS'
            },
            doubleClick: false
        };
        const action = new ClickWebAction({});
        expect(angular.toJson(action)).toEqual(angular.toJson(expectedAction));
    });
});
