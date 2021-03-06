import {Action} from "../../../../../src/js/entities/actions/action";
import {MoveMouseAction} from "../../../../../src/js/entities/actions/web/move-mouse-action";
import {actionType} from "../../../../../src/js/constants";

describe('MoveMouseAction', () => {
    beforeEach(angular.mock.module('ALEX'));

    it('should extend the default action and should implement a toString method', () => {
        const action = new MoveMouseAction({});
        expect(action instanceof Action).toBe(true);
        expect(angular.isFunction(action.toString)).toBe(true);
    });

    it('should create a default action', () => {
        const expectedAction = {
            type: actionType.WEB_MOUSE_MOVE,
            negated: false,
            ignoreFailure: false,
            disabled: false,

            node: {
                selector: null,
                type: 'CSS'
            },
            offsetX: 0,
            offsetY: 0
        };
        const action = new MoveMouseAction({});
        expect(angular.toJson(action)).toEqual(angular.toJson(expectedAction));
    });
});
